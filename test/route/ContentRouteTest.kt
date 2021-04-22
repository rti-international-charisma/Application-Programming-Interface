package com.rti.charisma.api.route

import com.rti.charisma.api.commonModule
import com.rti.charisma.api.contentModule
import com.rti.charisma.api.exception.ContentRequestException
import com.rti.charisma.api.fixtures.AssessmentFixture
import com.rti.charisma.api.fixtures.PageContentFixture
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import service.ContentService


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


    @Test
    fun `GET page should return 200 OK with json response`() = testApp {
        coEvery { contentService.getPage("assessment-intro") } returns PageContentFixture.withNoVideoSectionAndSteps("Published")

        handleRequest(HttpMethod.Get, "/assessment/intro") {
        }.apply {
            assertEquals(200, response.status()?.value)
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())
            assertEquals(PageContentFixture.pageWithoutVideoSectionJson(), response.content)
        }
    }

    @Test
    fun `GET page should return 200 OK with empty json response`() = testApp {
        coEvery { contentService.getPage("assessment-intro") } returns PageContentFixture.withNoVideoSectionAndSteps("Archived")

        handleRequest(HttpMethod.Get, "/assessment/intro") {
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
    fun `GET home should return 404 not found error for incorrect request`() = testApp {
        handleRequest(HttpMethod.Get, "/hmpage") {
        }.apply {
            assertEquals(404, response.status()?.value)

        }
    }

    @Test
    fun `GET page with pageId should throw 400 bad request error if error while fetching content`() = testApp {
        coEvery { contentService.getPage("assessment-intro") } throws ContentRequestException("some error")

        handleRequest(HttpMethod.Get, "/assessment/intro") {
        }.apply {
            assertEquals(400, response.status()?.value)
        }
    }

    @Test
    fun `GET page pageId should return 404 not found error for incorrect request`() = testApp {
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
        coEvery { contentService.getAssessment() } returns AssessmentFixture.archivedCmsContent()

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


    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        return withTestApplication({
            commonModule()
            contentModule(contentService)
        }) { callback() }
    }
}