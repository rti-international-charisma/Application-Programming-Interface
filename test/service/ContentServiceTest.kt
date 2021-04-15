package service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rti.charisma.api.client.CmsContent
import com.rti.charisma.api.client.ContentClient
import com.rti.charisma.api.exception.ContentException
import com.rti.charisma.api.exception.ContentRequestException
import com.rti.charisma.api.exception.NoContentAvailableException
import com.rti.charisma.api.model.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExperimentalCoroutinesApi
class ContentServiceTest {

    private val contentClient = mockk<ContentClient>(relaxed = true)
    private val contentService = ContentService(contentClient)

    @Test
    fun `it should parse homepage response if content in draft`() = runBlockingTest {
        val expectedHomePage = createHomePage()
        val content = draftContent()

        coEvery { contentClient.request("/items/homepage?fields=*.*.*") } returns content

        val homePage = contentService.getHomePage()

        assertEquals(expectedHomePage, homePage)
    }

    @Test
    fun `it should parse homepage response if content in published state`() = runBlockingTest {
        val expectedHomePage = createHomePage()
        val content = publishedContent()

        coEvery { contentClient.request("/items/homepage?fields=*.*.*") } returns content

        val homePage = contentService.getHomePage()

        assertEquals(expectedHomePage, homePage)
    }


    @Test
    fun `it should parse page response`() = runBlockingTest {
        val expectedPageContent = createPage()
        val content = pageContent()

        coEvery { contentClient.request("/items/pages/heartAssessmentIntro?fields=*.*.*") } returns content

        val pageContent = contentService.getPage("heartAssessmentIntro")

        assertEquals(expectedPageContent, pageContent)
    }

    @Test
    fun `it should throws exception on error processing response`() = runBlockingTest {
        coEvery { contentClient.request("/items/homepage?fields=*.*.*") } throws (ContentException("Content error"))
        assertFailsWith(
            exceptionClass = ContentException::class,
            block = { contentService.getHomePage() }
        )
    }

    @Test
    fun `it should throws exception on error fetching response`() = runBlockingTest {
        coEvery { contentClient.request("/items/homepage?fields=*.*.*") } throws (ContentRequestException("Content error"))
        assertFailsWith(
            exceptionClass = ContentRequestException::class,
            block = { contentService.getHomePage() }
        )
    }

    @Test
    fun `it should throw no content available exception if content not in allowed state `() = runBlockingTest {
        coEvery { contentClient.request("/items/homepage?fields=*.*.*") } returns archivedContent()
        assertFailsWith(
            exceptionClass = NoContentAvailableException::class,
            block = { contentService.getHomePage() }
        )
    }

    private fun archivedContent(): CmsContent {
        val content =  """{
	"data": {
		"id": "homepage",
		"title": "This is the landing page",
		"introduction": "This is introduction",
		"description": "This is description",
		"status": "archive",
		"hero_image": {
			"name": "hero image",
			"status": "published",
			"title": "Hero Image",
			"summary": "summary",
			"introduction": "<div><span>some styled introduction</span></div>",
			"image_url": "hero-image-id"
		}
	}
}"""
        return jacksonObjectMapper().readValue(content, CmsContent::class.java)
    }

    private fun pageContent(): CmsContent {
        val content =  """{
	"data": {
		"id": "intro page",
		"title": "This is the landing page",
		"introduction": "This is introduction",
		"summary": "This is description",
		"status": "published",
        "image_url": "/assets/image-id",
		"image": {
			"name": "Page image",
			"status": "published",
			"title": "Page Image",
			"summary": "summary",
			"introduction": "<div><span>some styled introduction</span></div>",
			"image_url": "page-image-id"
		}
	}
}"""
        return jacksonObjectMapper().readValue(content, CmsContent::class.java)
    }


    private fun publishedContent(): CmsContent {
        val content =  """{
	"data": {
		"id": "homepage",
		"title": "This is the landing page",
		"introduction": "This is introduction",
		"description": "This is description",
		"status": "published",
		"video_section": {
			"id": "video_section",
			"status": "published",
			"introduction": "Build a healthy relationship with your partner",
			"summary": "Here are some videos, activities and reading material for you",
			"videos": [{
					"id": "homepage_video",
					"status": "published",
					"video_url": "file1",
					"action_text": "action1",
					"title": "video-title1",
					"description": "description1",
					"video_section": "video_section",
                    "video_image": "video_image1"
				},
				{
					"id": "video_module2",
					"status": "published",
					"video_url": "file2",
					"action_text": "action2",
					"title": "video-title2",
					"description": "description2",
					"video_section": "video_section",
                    "video_image": "video_image2"
				}
			]
		},
		"steps": [{
				"id": 1,
				"title": "title-1",
				"sub_title": "sub-title-1",
				"background_image": {
					"id": "bg_image1",
					"title": "title",
					"type": "image/png",
					"description": "description"
				},
				"image": {
					"id": "image1",
					"title": "Ellipse 3",
					"description": null
				}
			},
			{
				"id": 2,
				"title": "title-2",
				"sub_title": "sub-title-2",
				"background_image": {
					"id": "bg_image2",
					"title": "Ellipse 4 (1)",
					"description": null
				},
				"image": {
					"id": "image2",
					"title": "Ellipse 10",
					"description": null
				}
			}
		],
        "images" : [
             {
                "name": " image 1",
                "status": "published",
                "title": "image1-title",
                "summary": "summary",
                "introduction": "intro",
                "image_url": "image1-id"
		    },
            {
                "name": " image 2",
                "status": "published",
                "title": "image2-title",
                "summary": "summary",
                "introduction": "intro",
                "image_url": "image2-id"
		    }
        ],
		"hero_image": {
			"name": "hero image",
			"status": "published",
			"title": "Hero Image",
			"summary": "summary",
			"introduction": "<div><span>some styled introduction</span></div>",
			"image_url": "hero-image-id"
		}
	}
}"""
        return jacksonObjectMapper().readValue(content, CmsContent::class.java)
    }

    private fun draftContent(): CmsContent {
        val content =  """{
	"data": {
		"id": "homepage",
		"title": "This is the landing page",
		"introduction": "This is introduction",
		"description": "This is description",
		"status": "draft",
		"video_section": {
			"id": "video_section",
			"status": "published",
			"introduction": "Build a healthy relationship with your partner",
			"summary": "Here are some videos, activities and reading material for you",
			"videos": [{
					"id": "homepage_video",
					"status": "published",
					"video_url": "file1",
					"action_text": "action1",
					"title": "video-title1",
					"description": "description1",
					"video_section": "video_section",
                    "video_image": "video_image1"
				},
				{
					"id": "video_module2",
					"status": "published",
					"video_url": "file2",
					"action_text": "action2",
					"title": "video-title2",
					"description": "description2",
					"video_section": "video_section",
                    "video_image": "video_image2"
				}
			]
		},
		"steps": [{
				"id": 1,
				"title": "title-1",
                "sub_title": "sub-title-1",
				"background_image": {
					"id": "bg_image1",
					"title": "title",
					"type": "image/png",
					"description": "description"
				},
				"image": {
					"id": "image1",
					"title": "Ellipse 3",
					"description": null
				}
			},
			{
				"id": 2,
				"title": "title-2",
				"sub_title": "sub-title-2",
				"background_image": {
					"id": "bg_image2",
					"title": "Ellipse 4 (1)",
					"description": null
				},
				"image": {
					"id": "image2",
					"title": "Ellipse 10",
					"description": null
				}
			}
		],
        "images" : [
             {
                "name": " image 1",
                "status": "published",
                "title": "image1-title",
                "summary": "summary",
                "introduction": "intro",
                "image_url": "image1-id"
		    },
            {
                "name": " image 2",
                "status": "published",
                "title": "image2-title",
                "summary": "summary",
                "introduction": "intro",
                "image_url": "image2-id"
		    }
        ],
		"hero_image": {
			"name": "hero image",
			"status": "published",
			"title": "Hero Image",
			"summary": "summary",
			"introduction": "<div><span>some styled introduction</span></div>",
			"image_url": "hero-image-id"
		}
	}
}"""
        return jacksonObjectMapper().readValue(content, CmsContent::class.java)
    }

    private fun createHomePage(): HomePage {
        val heroImage = PageImage("Hero Image", "<div><span>some styled introduction</span></div>", "summary", "/assets/hero-image-id")
        val image1 = PageImage("image1-title", "intro", "summary", "/assets/image1-id")
        val image2 = PageImage("image2-title", "intro", "summary", "/assets/image2-id")
        val video1 = PageVideo("video-title1", "description1", "/assets/file1", "/assets/video_image1")
        val video2 = PageVideo("video-title2", "description2", "/assets/file2", "/assets/video_image2")
        val step1 = Step("title-1", "sub-title-1", "/assets/bg_image1", "/assets/image1")
        val step2 = Step("title-2", "sub-title-2", "/assets/bg_image2", "/assets/image2")
        val videoSection = VideoSection(
            "Build a healthy relationship with your partner",
            "Here are some videos, activities and reading material for you",
            mutableListOf(video1, video2))
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

    private fun createPage(): Page {
        val image = PageImage("Page Image", "<div><span>some styled introduction</span></div>", "summary", "/assets/page-image-id")
        return Page(
            "This is the landing page",
            "This is description",
            "This is introduction",
            imageUrl = "/assets/image-id",
        )
    }
}