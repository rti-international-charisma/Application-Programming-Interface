package com.rti.charisma.api.service

import com.rti.charisma.api.client.ContentClient
import com.rti.charisma.api.content.Page
import com.rti.charisma.api.exception.ContentException
import com.rti.charisma.api.exception.ContentRequestException
import com.rti.charisma.api.exception.ContentServerException
import com.rti.charisma.api.fixtures.AssessmentFixture
import com.rti.charisma.api.fixtures.PageContentFixture
import com.rti.charisma.api.route.CONSENT
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExperimentalCoroutinesApi
class ContentServiceTest {

    private val contentClient = mockk<ContentClient>(relaxed = true)
    private val contentService = ContentService(contentClient)

    @After
    fun afterTests() {
        unmockkAll()
    }

    @Test
    fun `it should parse page response with video sections and steps`() = runBlockingTest {
        val expectedHomePage = PageContentFixture.withVideoSectionAndSteps()

        coEvery { contentClient.getPage("/items/homepage?fields=*.*.*") } returns PageContentFixture.pageFromCmsWithVideos()

        val homePage: Page = contentService.getHomePage()

        assertEquals(expectedHomePage, homePage)
    }

    @Test
    fun `it should parse page response for given page id with images and no video sections`() = runBlockingTest {
        val expectedPageContent = PageContentFixture.withNoVideoSectionAndSteps("published")
        val pageId = "test-page"

        coEvery {
            contentClient.getPage("/items/pages/${pageId}?fields=*.*.*")
        } returns PageContentFixture.pageFromCmsWithImages()

        val pageContent = contentService.getPage(pageId)

        assertEquals(expectedPageContent, pageContent)
    }

    @ParameterizedTest
    @ArgumentsSource(PrepScoreProvider::class)
    fun `it fetch prep counselling module based on score and consent`(
        score: Int,
        consent: CONSENT,
        moduleName: String
    ) = runBlockingTest {
        mockkObject(PrePModules)
        every { PrePModules.getModuleId(any()) } returns "moduleId"

        coEvery { contentClient.getPage(any()) } returns PageContentFixture.contentWithCounsellingModules()

        contentService.getModule(score, consent)

        coVerify {
            contentClient.getPage("/items/counselling_module/moduleId?fields=*.*,*.accordion_content.*")
        }

        verify { PrePModules.getModuleId(eq(moduleName)) }
    }

    @ParameterizedTest
    @ArgumentsSource(PrepScoreProvider::class)
    fun `it should parse page response for counselling modules`(score: Int, consent: CONSENT, moduleName: String) =
        runBlockingTest {
            mockkObject(PrePModules)
            every { PrePModules.getModuleId(any()) } returns "moduleId"

            val expectedPageContent = PageContentFixture.pageWithCounsellingModules("Published")

            coEvery {
                contentClient.getPage("/items/counselling_module/moduleId?fields=*.*,*.accordion_content.*")
            } returns PageContentFixture.contentWithCounsellingModules()

            val pageContent = contentService.getModule(score, consent)
            assertEquals(expectedPageContent, pageContent)

            verify { PrePModules.getModuleId(eq(moduleName)) }

        }

    @ParameterizedTest
    @ArgumentsSource(InvalidPrepScoreProvider::class)
    fun `it should throw content  exception if module name cannot be identified`(score: Int, consent: CONSENT) =
        runBlockingTest {
            assertFailsWith(
                exceptionClass = ContentRequestException::class,
                block = { contentService.getModule(score, consent) }
            )
            coVerify(exactly = 0, verifyBlock = { contentClient.getPage(any()) })
        }

    @Test
    fun `it should throw content exception if module not found`() = runBlockingTest {
        coEvery { contentClient.getPage(any()) } throws (ContentRequestException("Content not found"))
        assertFailsWith(
            exceptionClass = ContentRequestException::class,
            block = { contentService.getModule(13, CONSENT.OPPOSE) }
        )
    }

    @Test
    fun `it should throw content request exception if error fetching module content`() = runBlockingTest {
        coEvery { contentClient.getPage(any()) } throws (ContentRequestException("Content error"))
        assertFailsWith(
            exceptionClass = ContentRequestException::class,
            block = { contentService.getModule(13, CONSENT.OPPOSE) }
        )
    }

    @Test
    fun `it should throw exception on error processing response`() = runBlockingTest {
        coEvery { contentClient.getPage("/items/homepage?fields=*.*.*") } throws (ContentServerException(
            "Content error",
            Exception()
        ))
        assertFailsWith(
            exceptionClass = ContentServerException::class,
            block = { contentService.getHomePage() }
        )
    }

    @Test
    fun `it should throw unexpected exception on error processing response`() = runBlockingTest {
        coEvery { contentClient.getPage("/items/homepage?fields=*.*.*") } throws (ContentException(
            "Content error",
            RuntimeException()
        ))
        assertFailsWith(
            exceptionClass = ContentException::class,
            block = { contentService.getHomePage() }
        )
    }

    @Test
    fun `it should throw exception on error fetching response`() = runBlockingTest {
        coEvery { contentClient.getPage("/items/homepage?fields=*.*.*") } throws (ContentRequestException(
            "Content error"
        ))
        assertFailsWith(
            exceptionClass = ContentRequestException::class,
            block = { contentService.getHomePage() }
        )
    }

    @Test
    fun `it should throw exception on error processing page response`() = runBlockingTest {
        coEvery { contentClient.getPage("/items/pages/test-page?fields=*.*.*") } throws (ContentServerException(
            "Content error",
            Exception()
        ))
        assertFailsWith(
            exceptionClass = ContentServerException::class,
            block = { contentService.getPage("test-page") }
        )
    }

    @Test
    fun `it should throw exception on error fetching page response`() = runBlockingTest {
        coEvery { contentClient.getPage("/items/pages/test-page?fields=*.*.*") } throws (ContentRequestException(
            "Content Request Error"
        ))
        assertFailsWith(
            exceptionClass = ContentRequestException::class,
            block = { contentService.getPage("test-page") }
        )
    }

    @Test
    fun `it should parse assessment response`() = runBlockingTest {
        coEvery {
            contentClient.getAssessment(
                "/items/sections?sort=sort&fields=*,questions.questions_id.*,questions.questions_id.options.options_id.*"
            )
        } returns AssessmentFixture.assessmentCmsContent()

        val assessment = contentService.getAssessment()

        assertEquals(AssessmentFixture.assessment(), assessment)
    }

    @Test
    fun `it should throw exception on error processing assessment response`() = runBlockingTest {
        coEvery {
            contentClient.getAssessment(
                "/items/sections?sort=sort&fields=*,questions.questions_id.*,questions.questions_id.options.options_id.*"
            )
        } throws (ContentException("Content error", RuntimeException()))
        assertFailsWith(
            exceptionClass = ContentException::class,
            block = { contentService.getAssessment() }
        )
    }

    @Test
    fun `it should throw exception on error fetching assessment response`() = runBlockingTest {
        coEvery {
            contentClient.getAssessment(
                "/items/sections?sort=sort&fields=*,questions.questions_id.*,questions.questions_id.options.options_id.*"
            )
        } throws (ContentRequestException("Content Request Error"))
        assertFailsWith(
            exceptionClass = ContentRequestException::class,
            block = { contentService.getAssessment() }
        )
    }

    @Test
    fun `it should throw Content Server exception on error fetching assessment response`() = runBlockingTest {
        coEvery {
            contentClient.getAssessment(
                "/items/sections?sort=sort&fields=*,questions.questions_id.*,questions.questions_id.options.options_id.*"
            )
        } throws (ContentServerException("Content Request Error", RuntimeException()))
        assertFailsWith(
            exceptionClass = ContentServerException::class,
            block = { contentService.getAssessment() }
        )
    }
}

class PrepScoreProvider : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> {
        return Stream.of(
            Arguments.of(13, CONSENT.UNAWARE, PrePModules.PREP_ABUSE),
            Arguments.of(13, CONSENT.AGREE, PrePModules.PREP_ABUSE),
            Arguments.of(13, CONSENT.NEUTRAL, PrePModules.PREP_ABUSE),
            Arguments.of(13, CONSENT.OPPOSE, PrePModules.PREP_ABUSE),
            Arguments.of(42, CONSENT.AGREE, PrePModules.PREP_ABUSE),
            Arguments.of(1, CONSENT.AGREE, PrePModules.PREP_AGREE),
            Arguments.of(1, CONSENT.NEUTRAL, PrePModules.PREP_NEUTRAL),
            Arguments.of(1, CONSENT.UNAWARE, PrePModules.PREP_UNAWARE),
            Arguments.of(1, CONSENT.OPPOSE, PrePModules.PREP_OPPOSE),
            Arguments.of(12, CONSENT.AGREE, PrePModules.PREP_AGREE),
            Arguments.of(12, CONSENT.NEUTRAL, PrePModules.PREP_NEUTRAL),
            Arguments.of(12, CONSENT.UNAWARE, PrePModules.PREP_UNAWARE),
            Arguments.of(12, CONSENT.OPPOSE, PrePModules.PREP_OPPOSE)
        )
    }
}

class InvalidPrepScoreProvider : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> {
        return Stream.of(
            Arguments.of(50, CONSENT.UNAWARE),
            Arguments.of(-1, CONSENT.AGREE),
            Arguments.of(43, CONSENT.AGREE),
        )
    }
}
