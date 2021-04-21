package com.rti.charisma.api.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonIgnoreProperties(ignoreUnknown = true)
data class PageContent(
    @JsonProperty("data")
    val page: Page
)

@JsonIgnoreProperties(value = ["status"], allowSetters = true, ignoreUnknown = true)
data class Page(
    val title: String,
    val summary: String,
    val introduction: String,
    val images: List<ImagesInPage>
)

@JsonTypeName("directus_files_id")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
data class ImagesInPage(
    val title: String = "",
    @JsonProperty("id")
    val imageUrl: String = "",
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PageImage(
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