package com.rti.charisma.api.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class HomePageContent(
    @JsonProperty("data")
    val homepage: HomePage
)


@JsonIgnoreProperties(value = ["status"], allowSetters = true, ignoreUnknown = true)
data class HomePage(
    val title: String,
    val description: String,
    val status: String,
    val introduction: String,
    @JsonProperty("hero_image")
    val heroImage: PageImage,
    val images: List<PageImage>,
    @JsonProperty("video_section")
    val videoSection: VideoSection,
    val steps: List<Step>,
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
