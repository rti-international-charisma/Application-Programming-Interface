package com.rti.charisma.api.content

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.rti.charisma.api.content.serialiser.PageConversions

@JsonIgnoreProperties(ignoreUnknown = true)
data class PageContent(
    @JsonProperty("data")
    val page: Page
)

@JsonIgnoreProperties(value = ["status"], allowSetters = true, ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@JsonSerialize(using = PageConversions.Serializer::class)
data class Page(
    val id: String,
    val title: String?,
    val introduction: String?,
    val description: String?,
    val summary: String?,
    val status: String,
    @JsonProperty("hero_image", required = false)
    val heroImage: HeroImage?,
    @JsonAlias("hero_image_caption_test_complete")
    val heroImageCaptionTestComplete: String?,
    @JsonAlias("hero_image_caption_test_incomplete")
    val heroImageCaptionTestIncomplete: String?,
    @JsonProperty("images", required = false)
    val images: List<PageImage>?,
    @JsonProperty("documents", required = false)
    val documents: List<PageDocuments>?,
    @JsonProperty("video_section", required = false)
    val videoSection: VideoSection?,
    @JsonProperty(required = false)
    @JsonAlias("actions")
    val steps: List<Step>?,
    @JsonAlias("module_image")
    val moduleImage: CounsellingModuleImage?,
    @JsonAlias("counselling_module_sections", "sections")
    val counsellingSections: List<CounsellingSection>?,
    @JsonAlias("counselling_module_action_points", "action_points")
    val counsellingActionPoints: List<CounsellingActionPoint>?
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
data class PageImage(
    @JsonProperty("directus_files_id")
    val imageFile: ImageFile
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
data class PageDocuments(
    @JsonProperty("directus_files_id")
    val document: Document
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
data class ImageFile(
    @JsonProperty("id")
    val imageUrl: String = "",
    val title: String = ""
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
data class Document(
    @JsonProperty("id")
    val documentUrl: String = "",
    val title: String = "",
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
data class HeroImage(
    var title: String = "",
    var introduction: String? = "",
    @JsonProperty("image_url")
    var imageUrl: String = ""
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
data class PageVideo(
    var title: String? = "",
    var description: String? = "",
    @JsonProperty("video_url")
    var videoUrl: String?,
    @JsonProperty("video_image", required = false)
    var videoImage: String?,
    @JsonProperty("action_text")
    var actionText: String?,
    @JsonProperty("action_link")
    var actionLink: String?,
    @JsonProperty("is_private")
    var isPrivate: Boolean
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Step(
    val title: String,
    @JsonProperty("step_number")
    val stepNumber: Int,
    @JsonProperty("background_image")
    val backgroundImageUrl: String,
    @JsonProperty("image")
    val imageUrl: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class VideoSection(
    var introduction: String? = "",
    var summary: String? = "",
    var videos: List<PageVideo> = emptyList()
)
