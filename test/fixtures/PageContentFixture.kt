package com.rti.charisma.api.fixtures

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rti.charisma.api.content.*

object PageContentFixture {

    fun withVideoSectionAndSteps(): Page {
        val heroImage = HeroImage(
            "Hero Image",
            "<div><span>some styled introduction</span></div>",
            "summary",
            "hero-image-id"
        )
        val image1 = PageImage(ImageFile("image-1", "image-1 title"))
        val image2 = PageImage(ImageFile("image-2", "image-2 title"))
        val video1 = PageVideo("video-title1", "description1", "file1", "video_image1", "action1", "/assessment/intro", false)
        val video2 = PageVideo("video-title2", "description2", "file2", "video_image2", "action2", "/assessment/intro", false)
        val step1 = Step("title-1", "sub-title-1", "bg_image1", "image1")
        val step2 = Step("title-2", "sub-title-2", "bg_image2", "image2")
        val videoSection = VideoSection(
            "Build a healthy relationship with your partner",
            "Here are some videos, activities and reading material for you",
            mutableListOf(video1, video2)
        )
        return Page(
            "This is the landing page",
            "This is introduction",
            "This is description",
            "This is summary",
            "published",
            heroImage,
            mutableListOf(image1, image2),
            videoSection,
            mutableListOf(step1, step2)
        )

    }

    fun archivedContent(): PageContent {
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
        return jacksonObjectMapper().readValue(content, PageContent::class.java)
    }


    fun pageFromCmsWithVideos(): PageContent {
        val content = """{
	"data": {
		"id": "homepage",
		"title": "This is the landing page",
		"introduction": "This is introduction",
		"description": "This is description",
		"summary": "This is summary",
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
                    "action_link": "/assessment/intro",
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
                    "action_link": "/assessment/intro",
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
       "images": [{
                "id": 8,
                "pages_id": "intro",
                "directus_files_id": {
                    "id": "image-1",
                    "title": "image-1 title"
                }
            },
            {
                "id": 9,
                "pages_id": "intro",
                "directus_files_id": {
                    "id": "image-2",
                    "title": "image-2 title"
                }
         }],
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
        return jacksonObjectMapper().readValue(content, PageContent::class.java)
    }

    fun draftContent(): PageContent {
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
        return jacksonObjectMapper().readValue(content, PageContent::class.java)
    }


    fun pageWithVideoSectionResponseJson(): String {
        return """{
  "title" : "This is the landing page",
  "introduction" : "This is introduction",
  "description" : "This is description",
  "summary" : "This is summary",
  "heroImage" : {
    "title" : "Hero Image",
    "introduction" : "<div><span>some styled introduction</span></div>",
    "summary" : "summary",
    "imageUrl" : "/assets/hero-image-id"
  },
  "images" : [ {
    "title" : "image1-title",
    "imageUrl" : "/assets/image1-id"
  }, {
    "title" : "image2-title",
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
      "actionText" : "action1",
      "actionLink" : "/assessment/intro",
      "isPrivate" : false
    }, {
      "title" : "video-title2",
      "description" : "description2",
      "videoUrl" : "/assets/file2",
      "videoImage" : "/assets/video-image-2",
      "actionText" : "action2",
      "actionLink" : "/assessment/intro",
      "isPrivate" : false
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

    fun pageWithVideoSection(status: String): Page {
        val heroImage = HeroImage(
            "Hero Image",
            "<div><span>some styled introduction</span></div>",
            "summary",
            "hero-image-id"
        )
        val image1 = PageImage(ImageFile(title = "image1-title", imageUrl = "image1-id"))
        val image2 = PageImage(ImageFile(title = "image2-title", imageUrl = "image2-id"))
        val video1 = PageVideo("video-title1", "description1", "file1", "video-image-1", "action1", "/assessment/intro", false)
        val video2 = PageVideo("video-title2", "description2", "file2", "video-image-2", "action2", "/assessment/intro", false)
        val step1 = Step("title-1", "sub-title-1", "bg_image1", "image1")
        val step2 = Step("title-2", "sub-title-2", "bg_image2", "image2")
        val videoSection = VideoSection(
            "Build a healthy relationship with your partner",
            "Here are some videos, activities and reading material for you",
            mutableListOf(video1, video2)
        )
        return Page(
            "This is the landing page",
            "This is introduction",
            "This is description",
            "This is summary",
            status,
            heroImage,
            mutableListOf(image1, image2),
            videoSection,
            mutableListOf(step1, step2)
        )
    }

    fun withNoVideoSectionAndSteps(status: String): Page {
        return Page(
            "This is the landing page",
            "This is introduction",
            "This is description",
            "This is summary",
            status,
            null,
            images = mutableListOf(
                PageImage(ImageFile("image-1", "image-1 title")),
                PageImage(ImageFile("image-2", "image-2 title"))
            ),
            null,
            null
        )
    }


    fun archivedPageContent(): PageContent {
        val content = """{
            "data": {
		"id": "intro page",
		"title": "This is the landing page",
		"introduction": "This is introduction",
		"summary": "This is description",
		"status": "archived",
        "image_url": "/assets/image-id",
		"images": []
}
}"""
        return jacksonObjectMapper().readValue(content, PageContent::class.java)
    }

    fun pageFromCmsWithImages(): PageContent {
        val content = """{
            "data": {
		"id": "intro page",
		"title": "This is the landing page",
		"introduction": "This is introduction",
		"description": "This is description",
		"summary": "This is summary",
		"status": "published",
        "image_url": "image-id",
		"images": [{
                "id": 8,
                "pages_id": "intro",
                "directus_files_id": {
                    "id": "image-1",
                    "title": "image-1 title"
                }
            },
            {
                "id": 9,
                "pages_id": "intro",
                "directus_files_id": {
                    "id": "image-2",
                    "title": "image-2 title"
                }
            }]
}
}"""
        return jacksonObjectMapper().readValue(content, PageContent::class.java)
    }


    fun pageWithoutVideoSectionJson(): String {
        return """{
  "title" : "This is the landing page",
  "introduction" : "This is introduction",
  "description" : "This is description",
  "summary" : "This is summary",
  "images" : [ {
    "title" : "image-1 title",
    "imageUrl" : "/assets/image-1"
  }, {
    "title" : "image-2 title",
    "imageUrl" : "/assets/image-2"
  } ]
}"""
    }

}
