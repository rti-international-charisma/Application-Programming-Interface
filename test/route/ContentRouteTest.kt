package com.rti.charisma.api.route

import com.rti.charisma.api.commonModule
import com.rti.charisma.api.contentModule
import com.rti.charisma.api.exception.ContentException
import com.rti.charisma.api.exception.ContentRequestException
import com.rti.charisma.api.exception.ContentServerException
import com.rti.charisma.api.fixtures.AssessmentFixture
import com.rti.charisma.api.fixtures.PageContentFixture
import com.rti.charisma.api.service.ContentService
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class ContentRouteTest {

    private val contentService = mockk<ContentService>(relaxed = true)

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

    @ParameterizedTest
    @MethodSource("arguments")
    fun `GET page should return 200 OK with json response`(endPoint: String, pageId: String) = testApp {
        coEvery { contentService.getPage(pageId) } returns PageContentFixture.withNoVideoSectionAndSteps("Published")

        handleRequest(HttpMethod.Get, endPoint) {
        }.apply {
            assertEquals(200, response.status()?.value)
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())
            assertEquals(PageContentFixture.pageWithoutVideoSectionJson(), response.content)
        }
    }

    @Test
    fun `GET page should return 200 OK with counselling modules json response`() = testApp {
        coEvery {
            contentService.getModule(
                13,
                CONSENT.OPPOSE
            )
        } returns PageContentFixture.pageWithCounsellingModules("Published")

        handleRequest(HttpMethod.Get, "assessment/module?partner_score=13&prep_consent=oppose") {
        }.apply {
            assertEquals(200, response.status()?.value)
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())
            assertEquals(PageContentFixture.pageWithCounsellingResponseJson(), response.content)
        }
    }


    @Test
    fun `GET page should return 400 Bad request if score and consent missing`() = testApp {
        handleRequest(HttpMethod.Get, "assessment/module") {
        }.apply {
            assertEquals(400, response.status()?.value)
        }

        coVerify(verifyBlock = { contentService.getModule(any(), any()) }, exactly = 0)
    }

    @Test
    fun `GET page should return 400 Bad request if consent missing`() = testApp {
        handleRequest(HttpMethod.Get, "assessment/module?partner_score=13") {
        }.apply {
            assertEquals(400, response.status()?.value)
        }

        coVerify(verifyBlock = { contentService.getModule(any(), any()) }, exactly = 0)
    }

    @Test
    fun `GET page should return 400 Bad request if score  missing`() = testApp {
        handleRequest(HttpMethod.Get, "assessment/module?prep_consent=oppose") {
        }.apply {
            assertEquals(400, response.status()?.value)
        }

        coVerify(verifyBlock = { contentService.getModule(any(), any()) }, exactly = 0)
    }

    @Test
    fun `GET page should return 500 Error if score invalid`() = testApp {
        handleRequest(HttpMethod.Get, "assessment/module?partner_score=number&prep_consent=oppose") {
        }.apply {
            assertEquals(500, response.status()?.value)
        }

        coVerify(verifyBlock = { contentService.getModule(any(), any()) }, exactly = 0)
    }

    @Test
    fun `GET page should return 500 Error if consent invalid`() = testApp {
        handleRequest(HttpMethod.Get, "assessment/module?partner_score=21&prep_consent=yes") {
        }.apply {
            assertEquals(500, response.status()?.value)
        }

        coVerify(verifyBlock = { contentService.getModule(any(), any()) }, exactly = 0)
    }

    @Test
    fun `GET module without scores should return 200 OK with counselling modules json response`() = testApp {
        coEvery {
            contentService.getModuleWithoutScore("prep_use")
        } returns PageContentFixture.pageWithCounsellingModules("Published")

        handleRequest(HttpMethod.Get, "assessment/module/prep_use") {
        }.apply {
            assertEquals(200, response.status()?.value)
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())
            assertEquals(PageContentFixture.pageWithCounsellingResponseJson(), response.content)
        }
    }

    @Test
    fun `GET module without scores should return 400 Bad request if moduleID is missing`() = testApp {
        handleRequest(HttpMethod.Get, "assessment/module") {
        }.apply {
            assertEquals(400, response.status()?.value)
        }

        coVerify(verifyBlock = { contentService.getModuleWithoutScore(any()) }, exactly = 0)
    }

    @Test
    fun `GET module without scores should return 500 Bad request if moduleID is invalid`() = testApp {
        coEvery {
            contentService.getModuleWithoutScore("prep_use")
        } returns PageContentFixture.pageWithCounsellingModules("Published")

        handleRequest(HttpMethod.Get, "assessment/module/123") {
        }.apply {
            assertEquals(500, response.status()?.value)
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

    @Test
    fun `GET home should throw 400 bad request error if error while fetching content`() = testApp {
        coEvery { contentService.getHomePage() } throws ContentRequestException("some error")

        handleRequest(HttpMethod.Get, "/home") {
        }.apply {
            assertEquals(400, response.status()?.value)
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
    fun `GET home should return 404 not found error for incorrect request`() = testApp {
        handleRequest(HttpMethod.Get, "/hmpage") {
        }.apply {
            assertEquals(404, response.status()?.value)

        }
    }

    @ParameterizedTest
    @MethodSource("arguments")
    fun `GET page should throw 400 bad request error if error while fetching content`(
        endpoint: String,
        pageId: String
    ) = testApp {
        coEvery { contentService.getPage(pageId) } throws ContentRequestException("some error")

        handleRequest(HttpMethod.Get, endpoint) {
        }.apply {
            assertEquals(400, response.status()?.value)
        }
    }

    @ParameterizedTest
    @MethodSource("arguments")
    fun `GET assessment intro should throw 502 bad gateway error if error while fetching content`(
        endPoint: String,
        pageId: String
    ) = testApp {
        coEvery { contentService.getPage(pageId) } throws ContentServerException("some error", RuntimeException())

        handleRequest(HttpMethod.Get, endPoint) {
        }.apply {
            assertEquals(502, response.status()?.value)
        }
    }

    @Test
    fun `GET asessment intro should return 404 not found error for incorrect request`() = testApp {
        handleRequest(HttpMethod.Get, "/assessment/i") {
        }.apply {
            assertEquals(404, response.status()?.value)
        }
    }

    @Test
    fun `GET assessment should return 200 OK with json response`() = testApp {
        coEvery { contentService.getAssessment() } returns AssessmentFixture.assessment()

        handleRequest(HttpMethod.Get, "/assessment") {
        }.apply {
            assertEquals(200, response.status()?.value)
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())
            assertEquals(AssessmentFixture.assessmentResponseJson(), response.content)
        }
    }

    @Test
    fun `GET assessment should return 200 OK with only published sections`() = testApp {
        coEvery { contentService.getAssessment() } returns AssessmentFixture.archivedAssessmentCmsContent()

        handleRequest(HttpMethod.Get, "/assessment") {
        }.apply {
            assertEquals(200, response.status()?.value)
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())
            assertEquals(AssessmentFixture.onlyPublishedSectionsJson(), response.content)
        }
    }

    @Test
    fun `GET assessment should throw 400 bad request error if error while fetching content`() = testApp {
        coEvery { contentService.getAssessment() } throws ContentRequestException("some error")

        handleRequest(HttpMethod.Get, "/assessment") {
        }.apply {
            assertEquals(400, response.status()?.value)
        }
    }

    @Test
    fun `GET assessment should throw 500 bad request error if error while fetching content`() = testApp {
        coEvery { contentService.getAssessment() } throws ContentException("some error", RuntimeException())

        handleRequest(HttpMethod.Get, "/assessment") {
        }.apply {
            assertEquals(500, response.status()?.value)
        }
    }

    @Test
    fun `GET assessment should throw 502 bad gateway error if error while fetching content`() = testApp {
        coEvery { contentService.getAssessment() } throws ContentServerException("some error", RuntimeException())

        handleRequest(HttpMethod.Get, "/assessment") {
        }.apply {
            assertEquals(502, response.status()?.value)
        }
    }

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
                Arguments.of("assessment/intro", "assessment-intro"),
                Arguments.of("aboutus", "aboutus")
            )
    }
}