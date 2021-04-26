package com.rti.charisma.api.model

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
    val title: String,
    val introduction: String,
    val description: String,
    val summary: String,
    val status: String,
    @JsonProperty("hero_image", required = false)
    val heroImage: HeroImage?,
    @JsonProperty("images", required = false)
    val images: List<PageImage>?,
    @JsonProperty("video_section", required = false)
    val videoSection: VideoSection?,
    @JsonProperty(required = false)
    val steps: List<Step>?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PageImage(
    @JsonProperty("directus_files_id")
    val imageFile: ImageFile
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ImageFile(
    @JsonProperty("id")
    val imageUrl: String = "",
    val title: String = ""
)


@JsonIgnoreProperties(ignoreUnknown = true)
data class HeroImage(
    var title: String = "",
    var introduction: String = "",
    var summary: String = "",
    @JsonProperty("image_url")
    var imageUrl: String = ""
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PageVideo(
    var title: String = "",
    var description: String = "",
    @JsonProperty("video_url")
    var videoUrl: String = "",
    @JsonProperty("video_image")
    var videoImage: String = "",
    @JsonProperty("action_text")
    var actionText: String = ""
)


@JsonIgnoreProperties(ignoreUnknown = true)
data class Step(
    val title: String,
    @JsonProperty("sub_title") val subTitle: String,
    @JsonProperty("background_image") val backgroundImageUrl: String,
    @JsonProperty("image") val imageUrl: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class VideoSection(
    var introduction: String = "",
    var summary: String = "",
    var videos: List<PageVideo> = emptyList()
)
