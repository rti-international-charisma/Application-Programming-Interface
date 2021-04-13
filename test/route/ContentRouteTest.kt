package com.rti.charisma.api.route

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rti.charisma.api.client.CmsContent
import com.rti.charisma.api.commonModule
import com.rti.charisma.api.contentModule
import com.rti.charisma.api.exception.ContentRequestException
import com.rti.charisma.api.model.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import service.ContentService


class ContentRouteTest {

    private val contentService = mockk<ContentService>(relaxed = true)

    @Test
    fun `it should return 200 OK with json response`() = testApp {
        coEvery { contentService.getHomePage() } returns homepage()
        handleRequest(HttpMethod.Get, "/homepage") {
        }.apply {
            assertEquals(200, response.status()?.value)
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())
            assertNotNull(response.content)
            assertEquals(jsonResponse(), response.content)
        }
    }


    private fun jsonResponse(): String {
        return """{
  "title" : "This is the landing page",
  "description" : "This is description",
  "introduction" : "This is introduction",
  "heroImage" : {
    "title" : "Hero Image",
    "introduction" : "<div><span>some styled introduction</span></div>",
    "summary" : "summary",
    "imageUrl" : "/assets/hero-image-id"
  },
  "images" : [ {
    "title" : "image1-title",
    "introduction" : "intro",
    "summary" : "summary",
    "imageUrl" : "/assets/image1-id"
  }, {
    "title" : "image2-title",
    "introduction" : "intro",
    "summary" : "summary",
    "imageUrl" : "/assets/image2-id"
  } ],
  "videoSection" : {
    "introduction" : "Build a healthy relationship with your partner",
    "summary" : "Here are some videos, activities and reading material for you",
    "videos" : [ {
      "title" : "video-title1",
      "description" : "description1",
      "videoUrl" : "/assets/file1",
      "actionText" : "action1"
    }, {
      "title" : "video-title2",
      "description" : "description2",
      "videoUrl" : "/assets/file2",
      "actionText" : "action2"
    } ]
  },
  "steps" : [ {
    "title" : "title-1",
    "actionText" : "action1",
    "backgroundImageUrl" : "/assets/bg_image1",
    "imageUrl" : "/assets/image1"
  }, {
    "title" : "title-2",
    "actionText" : "action2",
    "backgroundImageUrl" : "/assets/bg_image2",
    "imageUrl" : "/assets/image2"
  } ]
}""" }


    private fun homepage(): HomePage {
        val heroImage = PageImage(
            "Hero Image",
            "<div><span>some styled introduction</span></div>",
            "summary",
            "/assets/hero-image-id"
        )
        val image1 = PageImage("image1-title", "intro", "summary", "/assets/image1-id")
        val image2 = PageImage("image2-title", "intro", "summary", "/assets/image2-id")
        val video1 = PageVideo("video-title1", "description1", "/assets/file1", "action1")
        val video2 = PageVideo("video-title2", "description2", "/assets/file2", "action2")
        val step1 = Step("title-1", "action1", "/assets/bg_image1", "/assets/image1")
        val step2 = Step("title-2", "action2", "/assets/bg_image2", "/assets/image2")
        val videoSection = VideoSection(
            "Build a healthy relationship with your partner",
            "Here are some videos, activities and reading material for you",
            mutableListOf(video1, video2)
        )
        return HomePage(
            "This is the landing page",
            "This is description",
            "This is introduction",
            heroImage,
            mutableListOf(image1, image2),
            videoSection,
            mutableListOf(step1, step2)
        )
    }


    @Test
    fun `it should throw 400 bad request error if error while fetching content`() = testApp {
        coEvery { contentService.getHomePage() } throws ContentRequestException("some error")

        handleRequest(HttpMethod.Get, "/homepage") {
        }.apply {
            assertEquals(400, response.status()?.value)
        }
    }

    @Test
    fun `it should return 404 not found error for incorrect request`() = testApp {
        handleRequest(HttpMethod.Get, "/hmpage") {
        }.apply {
            assertEquals(404, response.status()?.value)

        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        return withTestApplication({
            commonModule()
            contentModule(contentService)
        }) { callback() }
    }
}