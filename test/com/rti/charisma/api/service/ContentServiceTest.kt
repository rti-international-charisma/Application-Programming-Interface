package com.rti.charisma.api.service

import com.rti.charisma.api.client.ContentClient
import com.rti.charisma.api.content.Page
import com.rti.charisma.api.content.Referral
import com.rti.charisma.api.content.Referrals
import com.rti.charisma.api.exception.ContentException
import com.rti.charisma.api.exception.ContentRequestException
import com.rti.charisma.api.exception.ContentServerException
import com.rti.charisma.api.fixtures.AssessmentFixture
import com.rti.charisma.api.fixtures.PageContentFixture
import com.rti.charisma.api.fixtures.ReferralsFixture
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
import kotlin.test.assertNotEquals

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

        coEvery { contentClient.getPage("/items/homepage?fields=*.*,video_section.*.*") } returns PageContentFixture.fromCmsWithVideos()

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
            contentClient.getPage("/items/counselling_modules/moduleId?fields=*.*,video_section.*.*,*.accordions.*")
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
                contentClient.getPage("/items/counselling_modules/moduleId?fields=*.*,video_section.*.*,*.accordions.*")
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
        coEvery { contentClient.getPage("/items/homepage?fields=*.*,video_section.*.*") } throws (ContentServerException(
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
        coEvery { contentClient.getPage("/items/homepage?fields=*.*,video_section.*.*") } throws (ContentException(
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
        coEvery { contentClient.getPage("/items/homepage?fields=*.*,video_section.*.*") } throws (ContentRequestException(
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

    //----------------Assessment------------------//

    @Test
    fun `it should parse assessment response`() = runBlockingTest {
        coEvery {
            contentClient.getAssessment(
                "/items/assessment_sections?sort=sort&fields=*,questions.*,questions.options.options_id.*"
            )
        } returns AssessmentFixture.assessmentCmsContent()

        val assessment = contentService.getAssessments()

        assertEquals(AssessmentFixture.assessment(), assessment)
    }

    @Test
    fun `it should throw exception on error processing assessment response`() = runBlockingTest {
        coEvery {
            contentClient.getAssessment(
                "/items/assessment_sections?sort=sort&fields=*,questions.*,questions.options.options_id.*"
            )
        } throws (ContentException("Content error", RuntimeException()))
        assertFailsWith(
            exceptionClass = ContentException::class,
            block = { contentService.getAssessments() }
        )
    }

    @Test
    fun `it should throw exception on error fetching assessment response`() = runBlockingTest {
        coEvery {
            contentClient.getAssessment(
                "/items/assessment_sections?sort=sort&fields=*,questions.*,questions.options.options_id.*"
            )
        } throws (ContentRequestException("Content Request Error"))
        assertFailsWith(
            exceptionClass = ContentRequestException::class,
            block = { contentService.getAssessments() }
        )
    }

    @Test
    fun `it should throw Content Server exception on error fetching assessment response`() = runBlockingTest {
        coEvery {
            contentClient.getAssessment(
                "/items/assessment_sections?sort=sort&fields=*,questions.*,questions.options.options_id.*"
            )
        } throws (ContentServerException("Content Request Error", RuntimeException()))
        assertFailsWith(
            exceptionClass = ContentServerException::class,
            block = { contentService.getAssessments() }
        )
    }

    //----------------Referrals------------------//
    @Test
    fun `it should parse multiple referrals response`() = runBlockingTest {
        coEvery {
            contentClient.getReferrals("/items/referrals")
        } returns ReferralsFixture.cmsResponse()

        val assessment = contentService.getReferrals()

        assertEquals(ReferralsFixture.givenReferrals(), assessment)
    }

    @Test
    fun `it should parse referral response`() = runBlockingTest {
        coEvery {
            contentClient.getReferrals("/items/referrals?filter[type][_in]=Counselling")
        } returns ReferralsFixture.cmsResponseWithOneReferralType()

        val assessment = contentService.getReferrals("Counselling")

        assertEquals(Referrals(listOf(Referral(
            "Counselling",
            "Sophiatown Counseling",
            "Some address Code 32432432, 23423423423",
            "5a28b210-1697-4cc0-8c42-4d17ad0d8198"))), assessment)

        assertNotEquals(Referrals(listOf(Referral(
            "health",
            "Tara hospital",
            "50 Saxon Road, Hurlingham,\n" +
                    "011 535 3000, 323423324234",
            "5a28b210-1697-4cc0-8c42-4d17ad0d8198"))), assessment)
    }

    @Test
    fun `it should throw exception on error processing referrals`() = runBlockingTest {
        coEvery {
            contentClient.getReferrals("/items/referrals")
        } throws (ContentException("Content error", RuntimeException()))
        assertFailsWith(
            exceptionClass = ContentException::class,
            block = { contentService.getReferrals() }
        )
    }

    @Test
    fun `it should throw exception on error processing referrals when the TYPE filter parameter is present`() = runBlockingTest {
        coEvery {
            contentClient.getReferrals("/items/referrals?filter[type][_in]=ABC")
        } throws (ContentException("Content error", RuntimeException()))
        assertFailsWith(
            exceptionClass = ContentException::class,
            block = { contentService.getReferrals("ABC") }
        )
    }

    @Test
    fun `it should throw exception on error fetching referrals`() = runBlockingTest {
        coEvery {
            contentClient.getReferrals("/items/referrals")
        } throws (ContentRequestException("Content Request Error"))
        assertFailsWith(
            exceptionClass = ContentRequestException::class,
            block = { contentService.getReferrals() }
        )
    }

    @Test
    fun `it should throw exception on error fetching referrals when the TYPE filter parameter is present`() = runBlockingTest {
        coEvery {
            contentClient.getReferrals("/items/referrals?filter[type][_in]=ABC")
        } throws (ContentRequestException("Content Request Error"))
        assertFailsWith(
            exceptionClass = ContentRequestException::class,
            block = { contentService.getReferrals("ABC") }
        )
    }

    @Test
    fun `it should throw Content Server exception on error fetching referrals`() = runBlockingTest {
        coEvery {
            contentClient.getReferrals("/items/referrals")
        } throws (ContentServerException("Content Request Error", RuntimeException()))
        assertFailsWith(
            exceptionClass = ContentServerException::class,
            block = { contentService.getReferrals() }
        )
    }

    @Test
    fun `it should throw Content Server exception on error fetching referrals when the TYPE filter parameter is present`() = runBlockingTest {
        coEvery {
            contentClient.getReferrals("/items/referrals?filter[type][_in]=ABC")
        } throws (ContentServerException("Content Request Error", RuntimeException()))
        assertFailsWith(
            exceptionClass = ContentServerException::class,
            block = { contentService.getReferrals("ABC") }
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
