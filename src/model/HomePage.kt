package com.rti.charisma.api.model

import com.rti.charisma.api.config.CMS_ASSETS_URL
import com.rti.charisma.api.config.ConfigProvider
import com.rti.charisma.api.model.PageImage.Companion.toPageImage
import com.rti.charisma.api.model.PageVideo.Companion.toPageVideo
import com.rti.charisma.api.model.Step.Companion.toStep
import com.rti.charisma.api.model.VideoSection.Companion.toVideoSection
import java.util.stream.Collectors

data class HomePage(
    val title: String,
    val description: String,
    val introduction: String,
    val heroImage: PageImage,
    val images: List<PageImage>,
    val videoSection: VideoSection,
    val steps: List<Step>,
) {
    companion object {
        fun toHomePage(data: MutableMap<String, Any>): HomePage {
            return HomePage(
                title = (data["title"] ?: "") as String,
                introduction = (data["introduction"] ?: "") as String,
                description = (data["description"] ?: "") as String,
                heroImage = toPageImage(data["hero_image"]),
                images = toPageImages(data["images"]),
                videoSection = toVideoSection(data["video_section"]),
                steps = toSteps(data["steps"])
            )
        }

        private fun toPageImages(data: Any?): List<PageImage> {
            if (isListType(data)) {
                val images = data as List<Map<String, Any>>
                return images.stream()
                    .map { image -> toPageImage(image) }
                    .collect(Collectors.toList())
            }
            return emptyList()
        }

        private fun toSteps(data: Any?): List<Step> {
            if (isListType(data)) {
                val steps = data as List<Map<String, Any>>
                return steps.stream()
                    .map { step -> toStep(step) }
                    .collect(Collectors.toList())
            }
            return emptyList()
        }

        fun toPageVideos(data: Any?): List<PageVideo> {
            if (isListType(data)) {
                val videos = data as List<Map<String, Any>>
                return videos.stream()
                    .map { video -> toPageVideo(video) }
                    .collect(Collectors.toList())
            }
            return emptyList()
        }

        private fun isListType(data: Any?) = data !== null && data is List<*>
    }
}

data class Step(
    val title: String,
    val subTitle: String,
    val backgroundImageUrl: String,
    val imageUrl: String
) {
    companion object {
        fun toStep(data: Map<String, Any>): Step =
            Step(
                title = (data["title"] ?: "") as String,
                subTitle = (data["sub_title"] ?: "") as String,
                backgroundImageUrl = if (data["background_image"] != null) {
                    val image = data["background_image"] as String
                    "${ConfigProvider.get(CMS_ASSETS_URL)}/${image}"
                } else "",
                imageUrl = if (data["image"] != null) {
                    val image = data["image"] as String
                    "${ConfigProvider.get(CMS_ASSETS_URL)}/${image}"
                } else ""
            )
    }
}


data class VideoSection(
    var introduction: String = "",
    var summary: String = "",
    var videos: List<PageVideo> = emptyList()
) {
    companion object {
        fun toVideoSection(data: Any?): VideoSection {
            return if (data is Map<*, *>) {
                VideoSection(
                    introduction = (data["introduction"] ?: "") as String,
                    summary = (data["summary"] ?: "") as String,
                    videos = HomePage.toPageVideos(data["videos"]!!)
                )
            } else {
                VideoSection()
            }
        }
    }
}

