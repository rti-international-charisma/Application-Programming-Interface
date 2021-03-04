package com.rti.charisma.api

import com.contentful.java.cda.CDAClient
import com.rti.charisma.api.model.Asset
import com.rti.charisma.api.model.HomePage
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import service.ContentService
import kotlin.collections.HashMap
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
    fun `should return json content from content client on GET content `() = testApp {
        val homePage = actual()
        every { contentService.getHomePage() } returns homePage
        handleRequest(HttpMethod.Get, "/content").apply {

            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())
            assertEquals(expected(), response.content)
        }
    }

    private fun expected() = """{
  "title" : "test title",
  "contentBody" : "test content",
  "heroImage" : [ {
    "id" : "image id",
    "title" : "test image title",
    "url" : "image url",
    "mimeType" : "image mime"
  } ],
  "videos" : [ {
    "id" : "id1",
    "title" : "test video title1",
    "url" : "url1",
    "mimeType" : "video mime1"
  }, {
    "id" : "id2",
    "title" : "test video title2",
    "url" : "url2",
    "mimeType" : "video mime2"
  } ]
}"""

    private fun actual(): HomePage {
        val image = listOf(Asset ("image id", "test image title","image url", "image mime"))
        val videos = listOf(
            Asset ("id1", "test video title1","url1", "video mime1"),
            Asset ("id2", "test video title2","url2", "video mime2")  )
        return HomePage("test title", "test content", image, videos )
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({
            mainWithDependencies(
                contentClient,
                contentService
            )
        }){ callback()}
    }
}
