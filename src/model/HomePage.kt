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
    val heroImage: HeroImage,
    @JsonProperty("video_section")
    val videoSection: VideoSection,
    val steps: List<Step>
)
