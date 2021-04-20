package com.rti.charisma.api.fixtures

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rti.charisma.api.client.CmsContent
import com.rti.charisma.api.model.*

class HomePageFixture {

    fun homePageResult(): HomePage {
        val heroImage = PageImage(
            "Hero Image",
            "<div><span>some styled introduction</span></div>",
            "summary",
            "/assets/hero-image-id"
        )
        val image1 = PageImage("image1-title", "intro", "summary", "/assets/image1-id")
        val image2 = PageImage("image2-title", "intro", "summary", "/assets/image2-id")
        val video1 = PageVideo("video-title1", "description1", "/assets/file1", "/assets/video_image1", "action1")
        val video2 = PageVideo("video-title2", "description2", "/assets/file2", "/assets/video_image2", "action2")
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

    fun archivedContent(): CmsContent {
        val content = """{
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


    fun publishedContent(): CmsContent {
        val content = """{
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
				"background_image": "bg_image1",
				"image": "image1"
			},
			{
				"id": 2,
				"title": "title-2",
				"sub_title": "sub-title-2",
				"background_image": "bg_image2",
				"image": "image2"
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

    fun draftContent(): CmsContent {
        val content = """{
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
				"background_image": "bg_image1",
				"image": "image1"
			},
			{
				"id": 2,
				"title": "title-2",
				"sub_title": "sub-title-2",
				"background_image": "bg_image2",
				"image": "image2"
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


    fun homePageReponseJson(): String {
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

    fun homePageStubResponse(): HomePage {
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

}
