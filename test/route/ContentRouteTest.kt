package com.rti.charisma.api.route

import com.rti.charisma.api.commonModule
import com.rti.charisma.api.contentModule
import com.rti.charisma.api.exception.ContentRequestException
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
        coEvery { contentService.getHomePage() } returns homepage()
        handleRequest(HttpMethod.Get, "/home") {
        }.apply {
            assertEquals(200, response.status()?.value)
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())
            assertEquals(homePageJson(), response.content)
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

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        return withTestApplication({
            commonModule()
            contentModule(contentService)
        }) { callback() }
    }


    private fun homePageJson(): String {
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
      "videoImage" : "/assets/video-image-1",
      "actionText" : "action1"
    }, {
      "title" : "video-title2",
      "description" : "description2",
      "videoUrl" : "/assets/file2",
      "videoImage" : "/assets/video-image-2",
      "actionText" : "action2"
    } ]
  },
  "steps" : [ {
    "title" : "title-1",
    "subTitle" : "sub-title-1",
    "backgroundImageUrl" : "/assets/bg_image1",
    "imageUrl" : "/assets/image1"
  }, {
    "title" : "title-2",
    "subTitle" : "sub-title-2",
    "backgroundImageUrl" : "/assets/bg_image2",
    "imageUrl" : "/assets/image2"
  } ]
}"""
    }

    private fun introPageJson(): String {
        return """{
  "title" : "This is the landing page",
  "summary" : "This is description",
  "introduction" : "This is introduction",
  "imageUrl" : "/assets/image-id"
}"""
    }


    private fun homepage(): HomePage {
        val heroImage = PageImage(
            "Hero Image",
            "<div><span>some styled introduction</span></div>",
            "summary",
            "/assets/hero-image-id"
        )
        val image1 = PageImage("image1-title", "intro", "summary", "/assets/image1-id")
        val image2 = PageImage("image2-title", "intro", "summary", "/assets/image2-id")
        val video1 = PageVideo("video-title1", "description1", "/assets/file1", "/assets/video-image-1", "action1")
        val video2 = PageVideo("video-title2", "description2", "/assets/file2", "/assets/video-image-2", "action2")
        val step1 = Step("title-1", "sub-title-1", "/assets/bg_image1", "/assets/image1")
        val step2 = Step("title-2", "sub-title-2", "/assets/bg_image2", "/assets/image2")
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


    private fun introPage(): Page {
        return Page(
            "This is the landing page",
            "This is description",
            "This is introduction",
            "/assets/image-id"
        )
    }

}