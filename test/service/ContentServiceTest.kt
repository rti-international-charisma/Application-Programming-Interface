package service

import com.rti.charisma.api.client.ContentClient
import com.rti.charisma.api.content.Page
import com.rti.charisma.api.exception.ContentException
import com.rti.charisma.api.exception.ContentRequestException
import com.rti.charisma.api.fixtures.AssessmentFixture
import com.rti.charisma.api.fixtures.PageContentFixture
import com.rti.charisma.api.route.CONSENT
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ContentServiceTest {

    private val contentClient = mockk<ContentClient>(relaxed = true)
    private val contentService = ContentService(contentClient)

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
    fun `it fetch prep counselling module based on score and consent`(score: Int, consent: CONSENT, moduleName: String ) = runBlockingTest {
        coEvery { contentClient.getPage(any()) } returns PageContentFixture.contentWithCounsellingModules()
        contentService.getModule(score, consent)

        coVerify {
            contentClient.getPage("/items/counselling_module/${moduleName}?fields=*.*,*.accordion_content.*")
        }
    }

    @ParameterizedTest
    @ArgumentsSource(PrepScoreProvider::class)
    fun `it should parse page response for counselling modules`(score: Int, consent: CONSENT, moduleName: String) = runBlockingTest {
        val expectedPageContent = PageContentFixture.pageWithCounsellingModules("Published")

        coEvery {
            contentClient.getPage("/items/counselling_module/${moduleName}?fields=*.*,*.accordion_content.*")
        } returns PageContentFixture.contentWithCounsellingModules()

        val pageContent = contentService.getModule(score, consent)
        assertEquals(expectedPageContent, pageContent)
    }

    @Test
    fun `it should throw content exception if module not found`() = runBlockingTest {
        coEvery { contentClient.getPage(any()) } throws (ContentException("Content not found"))
        assertFailsWith(
            exceptionClass = ContentException::class,
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
    fun `it should throw content  exception if module name cannot be determined`() = runBlockingTest {
        assertFailsWith(
            exceptionClass = ContentException::class,
            block = { contentService.getModule(55, CONSENT.OPPOSE) }
        )
        coVerify ( exactly = 0, verifyBlock = {contentClient.getPage(any())})
    }

    @Test
    fun `it should throw exception on error processing response`() = runBlockingTest {
        coEvery { contentClient.getPage("/items/homepage?fields=*.*.*") } throws (ContentException("Content error"))
        assertFailsWith(
            exceptionClass = ContentException::class,
            block = { contentService.getHomePage() }
        )
    }

    @Test
    fun `it should throw exception on error fetching response`() = runBlockingTest {
        coEvery { contentClient.getPage("/items/homepage?fields=*.*.*") } throws (ContentRequestException("Content error"))
        assertFailsWith(
            exceptionClass = ContentRequestException::class,
            block = { contentService.getHomePage() }
        )
    }

    @Test
    fun `it should throw exception on error processing page response`() = runBlockingTest {
        coEvery { contentClient.getPage("/items/pages/test-page?fields=*.*.*") } throws (ContentException("Content error"))
        assertFailsWith(
            exceptionClass = ContentException::class,
            block = { contentService.getPage("test-page") }
        )
    }

    @Test
    fun `it should throw exception on error fetching page response`() = runBlockingTest {
        coEvery { contentClient.getPage("/items/pages/test-page?fields=*.*.*") } throws (ContentRequestException("Content Request Error"))
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
        } throws (ContentException("Content error"))
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


}

class PrepScoreProvider: ArgumentsProvider{
    override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> {
        return Stream.of(
            Arguments.of(13, CONSENT.UNAWARE, PREP_ABUSE),
            Arguments.of(42, CONSENT.UNAWARE, PREP_ABUSE),
            Arguments.of(12, CONSENT.AGREE, PREP_AGREE),
            Arguments.of(12, CONSENT.NEUTRAL, PREP_NEUTRAL),
            Arguments.of(12, CONSENT.UNAWARE, PREP_UNAWARE),
            Arguments.of(12, CONSENT.OPPOSE, PREP_OPPOSE)
        )
    }

}
