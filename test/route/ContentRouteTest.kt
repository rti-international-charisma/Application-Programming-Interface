package com.rti.charisma.api.route

import com.rti.charisma.api.commonModule
import com.rti.charisma.api.content.Assessment
import com.rti.charisma.api.contentModule
import com.rti.charisma.api.exception.ContentException
import com.rti.charisma.api.exception.ContentRequestException
import com.rti.charisma.api.exception.ContentServerException
import com.rti.charisma.api.fixtures.AssessmentFixture
import com.rti.charisma.api.fixtures.PageContentFixture
import com.rti.charisma.api.fixtures.ReferralsFixture
import com.rti.charisma.api.service.ContentService
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class ContentRouteTest {

    private val contentService = mockk<ContentService>(relaxed = true)

    //-------------------GET /home------------------------------------//

    @Test
    fun `GET home should return 200 OK with json response`() = testApp {
        coEvery { contentService.getHomePage() } returns PageContentFixture.pageWithVideoSection("Published")

        handleRequest(HttpMethod.Get, "/home") {
        }.apply {
            assertEquals(200, response.status()?.value)
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())
            assertEquals(PageContentFixture.pageWithVideoSectionResponseJson(), response.content)
        }
    }

    @Test
    fun `GET home should return 200 OK with empty content`() = testApp {
        coEvery { contentService.getHomePage() } returns PageContentFixture.pageWithVideoSection("Archived")

        handleRequest(HttpMethod.Get, "/home") {
        }.apply {
            assertEquals(200, response.status()?.value)
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())
            assertEquals("""{ }""", response.content)
        }
    }

    @Test
    fun `GET home should return 502 bad Gateway  if error while fetching content`() = testApp {
        coEvery { contentService.getHomePage() } throws ContentRequestException("some error")

        handleRequest(HttpMethod.Get, "/home") {
        }.apply {
            assertEquals(502, response.status()?.value)
        }
    }

    @Test
    fun `GET home should throw 502 bad gateway error if error while fetching content`() = testApp {
        coEvery { contentService.getHomePage() } throws ContentServerException("some error", RuntimeException())

        handleRequest(HttpMethod.Get, "/home") {
        }.apply {
            assertEquals(502, response.status()?.value)
        }
    }

    @Test
    fun `GET request should return 404 not found error for incorrect path`() = testApp {
        handleRequest(HttpMethod.Get, "/hmpage") {
        }.apply {
            assertEquals(404, response.status()?.value)

        }
    }

    //-------------------GET /page/{pageId}------------------------------------//

    @ParameterizedTest
    @MethodSource("arguments")
    fun `GET page should return 200 OK with json response`(endPoint: String, pageId: String) = testApp {
        coEvery { contentService.getPage(pageId) } returns PageContentFixture.withNoVideoSectionAndSteps("Published")

        handleRequest(HttpMethod.Get, endPoint) {
        }.apply {
            assertEquals(200, response.status()?.value)
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())
            assertEquals(PageContentFixture.pageWithoutVideoAndStepsJson(), response.content)
        }
    }

    @ParameterizedTest
    @MethodSource("arguments")
    fun `GET page should return with 502 bad gateway if error while fetching content`(
        endpoint: String,
        pageId: String
    ) = testApp {
        coEvery { contentService.getPage(pageId) } throws ContentRequestException("some error")

        handleRequest(HttpMethod.Get, endpoint) {
        }.apply {
            assertEquals(502, response.status()?.value)
        }
    }

    @ParameterizedTest
    @MethodSource("arguments")
    fun `GET page should return with 502 bad gateway  if error while fetching content`(
        endPoint: String,
        pageId: String
    ) = testApp {
        coEvery { contentService.getPage(pageId) } throws ContentServerException("some error", RuntimeException())

        handleRequest(HttpMethod.Get, endPoint) {
        }.apply {
            assertEquals(502, response.status()?.value)
        }
    }

    @ParameterizedTest
    @MethodSource("arguments")
    fun `GET page should return 200 OK with empty json response`(endPoint: String, pageId: String) = testApp {
        coEvery { contentService.getPage(pageId) } returns PageContentFixture.withNoVideoSectionAndSteps("Archived")

        handleRequest(HttpMethod.Get, endPoint) {
        }.apply {
            assertEquals(200, response.status()?.value)
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())
            assertEquals("""{ }""", response.content)
        }
    }


    //-------------------GET /modules?partner_score&prep_consent------------------------------------//


    @Test
    fun `GET modules with score and consent should return 200 OK with counselling modules json response`() = testApp {
        coEvery {
            contentService.getModule(any(), any())
        } returns PageContentFixture.pageWithCounsellingModules("Published")

        handleRequest(HttpMethod.Get, "/modules?partner_score=13&prep_consent=oppose") {

        }.apply {
            assertEquals(200, response.status()?.value)
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())
            assertEquals(PageContentFixture.pageWithCounsellingResponseJson(), response.content)
        }
    }

    @Test
    fun `GET modules with score parameter should return 200 OK with a empty json response`() = testApp {
        coEvery {
            contentService.getModule(any(), any())
        } returns PageContentFixture.pageWithCounsellingModules("archived")

        handleRequest(HttpMethod.Get, "/modules?partner_score=13&prep_consent=oppose") {

        }.apply {
            assertEquals(200, response.status()?.value)
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())
            assertEquals("""{ }""", response.content)
        }
    }

    @Test
    fun `GET modules with score parameter should Bad Request Response if score and consent missing`() = testApp {
        handleRequest(HttpMethod.Get, "/modules") {
        }.apply {
            assertEquals(400, response.status()?.value)
        }

        coVerify(verifyBlock = { contentService.getModule(any(), any()) }, exactly = 0)
    }

    @Test
    fun `GET modules with score parameter should Bad Request Response if consent missing`() = testApp {
        handleRequest(HttpMethod.Get, "/modules?partner_score=13") {
        }.apply {
            assertEquals(400, response.status()?.value)
        }

        coVerify(verifyBlock = { contentService.getModule(any(), any()) }, exactly = 0)
    }

    @Test
    fun `GET modules with score parameter should Bad Request Response  if score missing`() = testApp {
        handleRequest(HttpMethod.Get, "/modules?prep_consent=oppose") {
        }.apply {
            assertEquals(400, response.status()?.value)
        }

        coVerify(verifyBlock = { contentService.getModule(any(), any()) }, exactly = 0)
    }

    @Test
    fun `GET modules with score parameter should return 500 Error if score invalid`() = testApp {
        handleRequest(HttpMethod.Get, "/modules?partner_score=number&prep_consent=oppose") {
        }.apply {
            assertEquals(500, response.status()?.value)
        }

        coVerify(verifyBlock = { contentService.getModule(any(), any()) }, exactly = 0)
    }

    @Test
    fun `GET modules with score parameter should return 500 Error if consent invalid`() = testApp {
        handleRequest(HttpMethod.Get, "modules?partner_score=21&prep_consent=yes") {
        }.apply {
            assertEquals(500, response.status()?.value)
        }

        coVerify(verifyBlock = { contentService.getModule(any(), any()) }, exactly = 0)
    }

    @Test
    fun `GET modules with score parameter should return 500 Error if error fetching content`() = testApp {
        coEvery { contentService.getModule(any(), any()) } throws ContentException("some error", RuntimeException())

        handleRequest(HttpMethod.Get, "modules?partner_score=21&prep_consent=oppose") {
        }.apply {
            assertEquals(500, response.status()?.value)
        }
    }

    @Test
    fun `GET modules with score parameter should return 502 Error if error fetching content`() = testApp {
        coEvery { contentService.getModule(any(), any() ) } throws ContentServerException("some error", RuntimeException())

        handleRequest(HttpMethod.Get, "modules?partner_score=21&prep_consent=oppose") {
        }.apply {
            assertEquals(502, response.status()?.value)
        }

    }

    //-------------------GET /assessments------------------------------------//

    @Test
    fun `GET assessments should return 200 OK with json response`() = testApp {
        coEvery { contentService.getAssessments() } returns AssessmentFixture.assessment()

        handleRequest(HttpMethod.Get, "/assessments") {
        }.apply {
            assertEquals(200, response.status()?.value)
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())
            assertEquals(AssessmentFixture.assessmentResponseJson(), response.content)
        }
    }

    @Test
    fun `GET assessments should return 404 not found error for incorrect request`() = testApp {
        handleRequest(HttpMethod.Get, "/assessments/i") {
        }.apply {
            assertEquals(404, response.status()?.value)
        }
    }


    @Test
    fun `GET assessments should return 200 OK with only published sections`() = testApp {
        coEvery { contentService.getAssessments() } returns AssessmentFixture.archivedAssessmentCmsContent()

        handleRequest(HttpMethod.Get, "/assessments") {
        }.apply {
            assertEquals(200, response.status()?.value)
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())
            assertEquals(AssessmentFixture.onlyPublishedSectionsJson(), response.content)
        }
    }

    @Test
    fun `GET assessments should return 200 OK with empty response sections`() = testApp {
        coEvery { contentService.getAssessments() } returns Assessment(mutableListOf())

        handleRequest(HttpMethod.Get, "/assessments") {
        }.apply {
            assertEquals(200, response.status()?.value)
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())
            assertEquals(AssessmentFixture.emptyResponse(), response.content)
        }
    }

    @Test
    fun `GET assessments should return 200 OK with empty response given no published sections`() = testApp {
        coEvery { contentService.getAssessments() } returns AssessmentFixture.onlyArchived()

        handleRequest(HttpMethod.Get, "/assessments") {
        }.apply {
            assertEquals(200, response.status()?.value)
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())
            assertEquals(AssessmentFixture.emptyResponse(), response.content)
        }
    }

    @Test
    fun `GET assessments should return with 502 status if error while fetching content`() = testApp {
        coEvery { contentService.getAssessments() } throws ContentRequestException("some error")

        handleRequest(HttpMethod.Get, "/assessments") {
        }.apply {
            assertEquals(502, response.status()?.value)
        }
    }

    @Test
    fun `GET assessments should throw 500 bad request error if error while fetching content`() = testApp {
        coEvery { contentService.getAssessments() } throws ContentException("some error", RuntimeException())

        handleRequest(HttpMethod.Get, "/assessments") {
        }.apply {
            assertEquals(500, response.status()?.value)
        }
    }

    @Test
    fun `GET assessments should throw 502 bad gateway error if error while fetching content`() = testApp {
        coEvery { contentService.getAssessments() } throws ContentServerException("some error", RuntimeException())

        handleRequest(HttpMethod.Get, "/assessments") {
        }.apply {
            assertEquals(502, response.status()?.value)
        }
    }


    //-------------------GET /referrals------------------------------------//

    @Test
    fun `GET referrals should return 200 OK with list of referrals as json response`() = testApp {
        coEvery { contentService.getReferrals() } returns ReferralsFixture.givenReferrals()

        handleRequest(HttpMethod.Get, "/referrals") {
        }.apply {
            assertEquals(200, response.status()?.value)
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())
            assertEquals(ReferralsFixture.responseJson(), response.content)
        }
    }

    @Test
    fun `GET referrals should return 200 OK with empty json response if no referrals`() = testApp {
        coEvery { contentService.getReferrals() } returns ReferralsFixture.noReferrals()

        handleRequest(HttpMethod.Get, "/referrals") {
        }.apply {
            assertEquals(200, response.status()?.value)
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())
            assertEquals("""[ ]""", response.content)
        }
    }

    @Test
    fun `GET referrals should return 404 not found error for incorrect request`() = testApp {
        handleRequest(HttpMethod.Get, "/referrals/any") {
        }.apply {
            assertEquals(404, response.status()?.value)
        }
    }

    @Test
    fun `GET referrals should return with 502 status if error while fetching content`() = testApp {
        coEvery { contentService.getReferrals() } throws ContentRequestException("some error")

        handleRequest(HttpMethod.Get, "/referrals") {
        }.apply {
            assertEquals(502, response.status()?.value)
        }
    }

    @Test
    fun `GET referrals should throw 500 bad request error if error while fetching content`() = testApp {
        coEvery { contentService.getReferrals() } throws ContentException("some error", RuntimeException())

        handleRequest(HttpMethod.Get, "/referrals") {
        }.apply {
            assertEquals(500, response.status()?.value)
        }
    }

    @Test
    fun `GET referrals should throw 502 bad gateway error if error while fetching content`() = testApp {
        coEvery { contentService.getReferrals() } throws ContentServerException("some error", RuntimeException())

        handleRequest(HttpMethod.Get, "/referrals") {
        }.apply {
            assertEquals(502, response.status()?.value)
        }
    }


    //-------------------GET /modules/{moduleId}------------------------------------//



    @Test
    fun `GET modules for a given module if should return 200 OK with counselling module json response`() = testApp {
        coEvery {
            contentService.getModule("prep-use")
        } returns PageContentFixture.pageWithCounsellingModules("Published")

        handleRequest(HttpMethod.Get, "/modules/prep-use") {

        }.apply {
            assertEquals(200, response.status()?.value)
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())
            assertEquals(PageContentFixture.pageWithCounsellingResponseJson(), response.content)
        }
    }

    @Test
    fun `GET modules for given module id should return 502 Bad Gateway if module id missing`() = testApp {
        coEvery { contentService.getModule(any())
        } throws ContentServerException("Error", RuntimeException("Some runtime exception"))
        handleRequest(HttpMethod.Get, "/modules/eee") {
        }.apply {
            assertEquals(502, response.status()?.value)
        }

    }

    @Test
    fun `GET modules for given module id should return 500 Server Error if module id missing`() = testApp {
        coEvery { contentService.getModule(any())
        } throws ContentException("Error", RuntimeException("Some runtime exception"))
        handleRequest(HttpMethod.Get, "/modules/eee") {
        }.apply {
            assertEquals(500, response.status()?.value)
        }

    }


    //------------------------------------------------------------------------------//

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        return withTestApplication({
            commonModule()
            contentModule(contentService)
        }) { callback() }
    }

    private companion object {
        @JvmStatic
        fun arguments() =
            Stream.of(
                Arguments.of("content/assessment-intro", "assessment-intro"),
                Arguments.of("content/aboutus", "aboutus")
            )
    }
}