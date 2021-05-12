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
            "homepage",
            "This is the landing page",
            "This is introduction",
            "This is description",
            "This is summary",
            "published",
            heroImage,
            mutableListOf(image1, image2),
            videoSection,
            mutableListOf(step1, step2),
            null,
            null,
            null,
            null
        )

    }

    fun fromCmsWithVideos(): PageContent {
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
            "pageId",
            "This is the landing page",
            "This is introduction",
            "This is description",
            "This is summary",
            status,
            heroImage,
            mutableListOf(image1, image2),
            videoSection,
            mutableListOf(step1, step2),
            null,
            null,
            null,
            null
        )
    }

    fun withNoVideoSectionAndSteps(status: String): Page {
        return Page(
            "intro page",
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
            null,
            null,
            null,
            null,
            null
        )
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


    fun pageWithoutVideoAndStepsJson(): String {
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

    fun contentWithCounsellingModules(): PageContent {
        val content = """{
            "data" : {
  "id": "prep-use",
  "title": "Discussing PrEP Use With Partners",
  "introduction": "<p>Bring about positive changes in your relationship through better communication</p>",
  "status": "Published",
  "hero_image": {
    "title": "PrEp Hero Image",
    "image_url": "1c2eea87-f593-41c2-b6ba-da69a3133c9a"
  },
  "video_url": {
    "id": "videourl"
  },
  "module_image": {
    "id": "moduleimage"
  },
  "counselling_module_action_points": [{
    "id": "model_prep_use_action_point_1",
    "module_name": "prep_use",
    "title": "Make a decision about whether to tell my partner about PrEP or not"
  },
    {
      "id": "model_prep_use_action_point_2",
      "module_name": "prep_use",
      "title": "Practice how I'll respond to my partner's concerns about PrEP"
    },
    {
      "id": "model_prep_use_action_point_3",
       "module_name": "prep_use",
      "title": "Prepare for telling my partner (e.g. decide where and when I'll do it)"
    },
    {
      "id": "model_prep_use_action_point_4",
      "module_name": "prep_use",
      "title": "Share materials from this site with my partner"
    },
    {
      "id": "model_prep_use_action_point_5",
      "module_name": "prep_use",
      "title": "Call a PrEP clinic to ask about partner counselling"
    },
    {
      "id": "model_prep_use_action_point_6",
      "module_name": "prep_use",
      "title": "Make a plan to keep my PrEP secret (e.g. where to store it and when to take it)"
    }
  ],
  "counselling_module_sections": [{
    "id": "section_4",
    "title": "How to use PrEP without anyone knowing",
    "introduction": "<p>Sometimes it makes sense not to tell your partner, or anyone else about your PrEP use</p>",
    "summary": null,
    "accordion_content": [{
      "id": "section_4_accordion_1",
      "title": "Here are some tips that other young women have used:",
      "description": "<p>● Store pills in places your partner</p>"
    }]
  },
    {
      "id": "section_5",
      "title": "Why you decided to use oral PrEP",
      "introduction": "<p>If talking to a partner, the benefits for the relationship</p>",
      "summary": null,
      "accordion_content": null
    },
    {
      "id": "section_1",
      "title": "Should I tell my partner, or someone else I love, I'm taking PrEP?",
      "introduction": "<p><em>There are many things that other women like you think about when deciding whether to tell reasons</p>",
      "summary": "<p>Remember, whether you share or not is optional. </p>",
      "accordion_content": [{
        "id": "section_1_accordion_1",
        "title": "Some common reasons for sharing are:",
        "description": "<p>● You feel like you need your partner&rsquo;s, </p>"
      },
        {
          "id": "section_1_accordion_2",
          "title": "On the other hand, some reasons for NOT sharing include:",
          "description": "<p>● You worry your partner, or someone else you tell, may not allow you to use PrEP or force you to stop using it<br />● </p>"
        }
      ]
    }
  ]
  }
}"""
        return jacksonObjectMapper().readValue(content, PageContent::class.java)

    }

    fun pageWithCounsellingModules(status: String): Page {
        val heroImage = HeroImage(
            "PrEp Hero Image",
            "",
            "",
            "1c2eea87-f593-41c2-b6ba-da69a3133c9a"
        )
        val actionPoint1 = CounsellingModuleActionPoint(
            "model_prep_use_action_point_1",
            "Make a decision about whether to tell my partner about PrEP or not",
            "prep_use"
        )
        val actionPoint2 = CounsellingModuleActionPoint(
            "model_prep_use_action_point_2",
            "Practice how I'll respond to my partner's concerns about PrEP",
            "prep_use"
        )
        val actionPoint3 = CounsellingModuleActionPoint(
            "model_prep_use_action_point_3",
            "Prepare for telling my partner (e.g. decide where and when I'll do it)",
            "prep_use"
        )
        val actionPoint4 = CounsellingModuleActionPoint(
            "model_prep_use_action_point_4",
            "Share materials from this site with my partner",
            "prep_use"
        )
        val actionPoint5 = CounsellingModuleActionPoint(
            "model_prep_use_action_point_5",
            "Call a PrEP clinic to ask about partner counselling",
            "prep_use"
        )
        val actionPoint6 = CounsellingModuleActionPoint(
            "model_prep_use_action_point_6",
            "Make a plan to keep my PrEP secret (e.g. where to store it and when to take it)",
            "prep_use"
        )
        val counsellingActionPoints =
            listOf(actionPoint1, actionPoint2, actionPoint3, actionPoint4, actionPoint5, actionPoint6)
        val moduleVideo = CounsellingModuleVideo(
            "videourl"
        )
        val moduleImage = CounsellingModuleImage(
            "moduleimage"
        )
        val section1 = CounsellingModuleSection(
            "section_4",
            "How to use PrEP without anyone knowing",
            "<p>Sometimes it makes sense not to tell your partner, or anyone else about your PrEP use</p>",
            null,
            null,
            listOf(
                AccordionContent(
                    "section_4_accordion_1",
                    "Here are some tips that other young women have used:",
                    "<p>● Store pills in places your partner</p>"
                )
            )
        )
        val section2 = CounsellingModuleSection(
            "section_5",
            "Why you decided to use oral PrEP",
            "<p>If talking to a partner, the benefits for the relationship</p>",
            null,
            null,
            null
        )
        val section3 = CounsellingModuleSection(
            "section_1",
            "Should I tell my partner, or someone else I love, I'm taking PrEP?",
            "<p><em>There are many things that other women like you think about when deciding whether to tell reasons</p>",
            "<p>Remember, whether you share or not is optional. </p>",
            null,
            listOf(
                AccordionContent(
                    "section_1_accordion_1",
                    "Some common reasons for sharing are:",
                    "<p>● You feel like you need your partner&rsquo;s, </p>"
                ),
                AccordionContent(
                    "section_1_accordion_2",
                    "On the other hand, some reasons for NOT sharing include:",
                    "<p>● You worry your partner, or someone else you tell, may not allow you to use PrEP or force you to stop using it<br />● </p>"
                )
            )
        )
        val counsellingSections = listOf(section1, section2, section3)
        return Page(
            "prep-use",
            "Discussing PrEP Use With Partners",
            "<p>Bring about positive changes in your relationship through better communication</p>",
            null,
            null,
            status,
            heroImage,
            null,
            null,
            null,
            moduleVideo,
            moduleImage,
            counsellingSections,
            counsellingActionPoints

        )
    }

    fun pageWithCounsellingResponseJson(): String {
        return """{
  "title" : "Discussing PrEP Use With Partners",
  "introduction" : "<p>Bring about positive changes in your relationship through better communication</p>",
  "description" : null,
  "summary" : null,
  "heroImage" : {
    "title" : "PrEp Hero Image",
    "introduction" : "",
    "summary" : "",
    "imageUrl" : "/assets/1c2eea87-f593-41c2-b6ba-da69a3133c9a"
  },
  "moduleVideo" : {
    "videoUrl" : "/assets/videourl"
  },
  "moduleImage" : {
    "imageUrl" : "/assets/moduleimage"
  },
  "counsellingModuleSections" : [ {
    "id" : "section_4",
    "title" : "How to use PrEP without anyone knowing",
    "introduction" : "<p>Sometimes it makes sense not to tell your partner, or anyone else about your PrEP use</p>",
    "summary" : null,
    "accordionContent" : [ {
      "id" : "section_4_accordion_1",
      "description" : "<p>● Store pills in places your partner</p>",
      "title" : "Here are some tips that other young women have used:"
    } ]
  }, {
    "id" : "section_5",
    "title" : "Why you decided to use oral PrEP",
    "introduction" : "<p>If talking to a partner, the benefits for the relationship</p>",
    "summary" : null
  }, {
    "id" : "section_1",
    "title" : "Should I tell my partner, or someone else I love, I'm taking PrEP?",
    "introduction" : "<p><em>There are many things that other women like you think about when deciding whether to tell reasons</p>",
    "summary" : "<p>Remember, whether you share or not is optional. </p>",
    "accordionContent" : [ {
      "id" : "section_1_accordion_1",
      "description" : "<p>● You feel like you need your partner&rsquo;s, </p>",
      "title" : "Some common reasons for sharing are:"
    }, {
      "id" : "section_1_accordion_2",
      "description" : "<p>● You worry your partner, or someone else you tell, may not allow you to use PrEP or force you to stop using it<br />● </p>",
      "title" : "On the other hand, some reasons for NOT sharing include:"
    } ]
  } ],
  "counsellingModuleActionPoints" : [ {
    "id" : "model_prep_use_action_point_1",
    "title" : "Make a decision about whether to tell my partner about PrEP or not"
  }, {
    "id" : "model_prep_use_action_point_2",
    "title" : "Practice how I'll respond to my partner's concerns about PrEP"
  }, {
    "id" : "model_prep_use_action_point_3",
    "title" : "Prepare for telling my partner (e.g. decide where and when I'll do it)"
  }, {
    "id" : "model_prep_use_action_point_4",
    "title" : "Share materials from this site with my partner"
  }, {
    "id" : "model_prep_use_action_point_5",
    "title" : "Call a PrEP clinic to ask about partner counselling"
  }, {
    "id" : "model_prep_use_action_point_6",
    "title" : "Make a plan to keep my PrEP secret (e.g. where to store it and when to take it)"
  } ]
}"""
    }

}
