package com.rti.charisma.api

import com.contentful.java.cda.CDAClient
import com.rti.charisma.api.model.Asset
import com.rti.charisma.api.model.HomePage
import com.rti.charisma.api.service.UserService
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import service.ContentService
import kotlin.test.assertEquals

class ApplicationTest {
    private val contentClient = mockk<CDAClient>(relaxed = true)
    private val contentService = mockk<ContentService>(relaxed = true)

    @Test
    fun `should return text response on GET `() = testApp {
        handleRequest(HttpMethod.Get, "/").apply {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Try /content", response.content)
        }
    }

    @Test
    fun `should return NOT_FOUND response on invlaid rquest`() = testApp {
        handleRequest(HttpMethod.Get, "/invalid").apply {
            assertEquals(HttpStatusCode.NotFound, response.status())
        }
    }

    @Test
    fun `should return json content from content client on GET content `() = testApp {
        val homePage = actual()
        every { contentService.getHomePage() } returns homePage
        handleRequest(HttpMethod.Get, "/content").apply {

            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())
            assertEquals(expected(), response.content)
        }
    }

    @Test
    fun `should return internal server error on error from CDA Client on GET content `() = testApp {
        every { contentService.getHomePage() } throws Exception("Error connecting to CMS")
        handleRequest(HttpMethod.Get, "/content").apply {

            assertEquals(HttpStatusCode.InternalServerError, response.status())
        }
    }


    private fun expected() = """{
  "textContent" : {
    "test" : "title"
  },
  "assets" : {
    "assets" : [ {
      "id" : "id1",
      "title" : "test video title1",
      "url" : "url1",
      "mimeType" : "video mime1"
    }, {
      "id" : "id2",
      "title" : "test video title2",
      "url" : "url2",
      "mimeType" : "video mime2"
    }, {
      "id" : "image id",
      "title" : "test image title",
      "url" : "image url",
      "mimeType" : "image mime"
    } ]
  }
}"""

    private fun actual(): HomePage {
        val videos = listOf(
            Asset ("id1", "test video title1","url1", "video mime1"),
            Asset ("id2", "test video title2","url2", "video mime2"),
            Asset ("image id", "test image title","image url", "image mime"))
        return HomePage(mutableMapOf("test" to "title"), mutableMapOf("assets" to (videos)))
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({
            commonModule()
            cmsModule(
                contentClient,
                contentService
            )
        }){ callback()}
    }
}
