package com.rti.charisma.api.route

import com.rti.charisma.api.commonModule
import com.rti.charisma.api.contentModule
import com.rti.charisma.api.exception.ContentRequestException
import com.rti.charisma.api.fixtures.AssessmentFixture
import com.rti.charisma.api.fixtures.HomePageFixture
import com.rti.charisma.api.model.*
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
        coEvery { contentService.getHomePage() } returns HomePageFixture().homePageStubResponse()
        handleRequest(HttpMethod.Get, "/home") {
        }.apply {
            assertEquals(200, response.status()?.value)
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())
            assertEquals(HomePageFixture().homePageReponseJson(), response.content)
        }
    }

    @Test
    fun `GET assessment-intro should return 200 OK with json response`() = testApp {
        coEvery { contentService.getPage("assessment-intro") } returns introPage()

        handleRequest(HttpMethod.Get, "/assessment/intro") {
        }.apply {
            assertEquals(200, response.status()?.value)
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())
            assertEquals(introPageJson(), response.content)
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
    fun `GET assessment-intro should throw 400 bad request error if error while fetching content`() = testApp {
        coEvery { contentService.getPage("assessment-intro") } throws ContentRequestException("some error")

        handleRequest(HttpMethod.Get, "/assessment/intro") {
        }.apply {
            assertEquals(400, response.status()?.value)
        }
    }

    @Test
    fun `GET assessment-intro  should return 404 not found error for incorrect request`() = testApp {
        handleRequest(HttpMethod.Get, "/assessment/i") {
        }.apply {
            assertEquals(404, response.status()?.value)
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



    private fun introPageJson(): String {
        return """{
  "title" : "This is the landing page",
  "summary" : "This is description",
  "introduction" : "This is introduction",
  "images" : [ {
    "title" : "Image 1",
    "imageUrl" : "/assets/image1"
  }, {
    "title" : "Image 2",
    "imageUrl" : "/assets/image2"
  } ]
}"""
    }

    private fun introPage(): Page {
        return Page(
            "This is the landing page",
            "This is description",
            "This is introduction",
            mutableListOf(ImagesInPage("Image 1", "/assets/image1"), ImagesInPage("Image 2", "/assets/image2"))
        )
    }

}