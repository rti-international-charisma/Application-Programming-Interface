package com.rti.charisma.api.model

import com.rti.charisma.api.config.CMS_ASSETS_URL
import com.rti.charisma.api.config.ConfigProvider
import java.util.stream.Collectors

data class Page(
    val title: String,
    val summary: String,
    val introduction: String,
    val images: List<ImagesInPage>
) {
    companion object {
        fun toPage(data: Map<String, Any>): Page {
            return Page(
                title = (data["title"] ?: "") as String,
                introduction = (data["introduction"] ?: "") as String,
                summary = (data["summary"] ?: "") as String,
                images = (data["images"] as List<Map<String, Any>>).stream()
                    .map { imageData -> ImagesInPage.toImagesInPage(imageData["directus_files_id"] as Map<String, Any>) }
                    .collect(Collectors.toList())
            )
        }
    }
}

data class ImagesInPage(
    val title: String = "",
    val imageUrl: String = "",
) {
    companion object {
        fun toImagesInPage(data: Map<String, Any>): ImagesInPage {
            return ImagesInPage(
                title = (data["title"] ?: "") as String,
                imageUrl = if (data["id"] != null) "${ConfigProvider.get(CMS_ASSETS_URL)}/${data["id"]}" else ""
            )
        }
    }
}

data class PageImage(
    var title: String = "",
    var introduction: String = "",
    var summary: String = "",
    var imageUrl: String = ""
) {
    companion object {
        fun toPageImage(data: Any?): PageImage {
            return if (data is Map<*, *>) {
                PageImage(
                    title = (data["title"] ?: "") as String,
                    introduction = (data["introduction"] ?: "") as String,
                    summary = (data["summary"] ?: "") as String,
                    imageUrl = if (data["image_url"] != null) "${ConfigProvider.get(CMS_ASSETS_URL)}/${data["image_url"]}" else ""
                )
            } else {
                PageImage()
            }
        }
    }
}

data class PageVideo(
    var title: String = "",
    var description: String = "",
    var videoUrl: String = "",
    var videoImage: String = "",
    var actionText: String = ""
) {
    companion object {
        fun toPageVideo(data: Any): PageVideo {
            return if (data is Map<*, *>) {
                PageVideo(
                    title = (data["title"] ?: "") as String,
                    description = (data["description"] ?: "") as String,
                    videoUrl = if (data["video_url"] != null) "${ConfigProvider.get(CMS_ASSETS_URL)}/${data["video_url"]}" else "",
                    videoImage = if (data["video_image"] != null) "${ConfigProvider.get(CMS_ASSETS_URL)}/${data["video_image"]}" else "",
                    actionText = (data["action_text"] ?: "") as String
                )
            } else {
                return PageVideo()
            }
        }
    }
}
