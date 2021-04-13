package com.rti.charisma.api.model

import com.rti.charisma.api.config.CMS_ASSETS_URL
import com.rti.charisma.api.config.ConfigProvider

data class Page(
    val title: String,
    val description: String,
    val introduction: String,
    val image: PageImage
) {
    companion object {
        fun toPage(data: MutableMap<String, Any>): Page {
            return Page(
                title = (data["title"] ?: "") as String,
                introduction = (data["introduction"] ?: "") as String,
                description = (data["description"] ?: "") as String,
                image = PageImage.toPageImage(data["image"])
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
                    imageUrl = if (data["image_file"] != null) "${ConfigProvider.get(CMS_ASSETS_URL)}/${data["image_file"]}" else ""
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
    var actionText: String = ""
) {
    companion object {
        fun toPageVideo(data: Any): PageVideo {
            return if (data is Map<*, *>) {
                PageVideo(
                    title = (data["title"] ?: "") as String,
                    description = (data["description"] ?: "") as String,
                    videoUrl = if (data["video_file"] != null) "${ConfigProvider.get(CMS_ASSETS_URL)}/${data["video_file"]}" else "",
                    actionText = (data["action_text"] ?: "") as String
                )
            } else {
                return PageVideo()
            }
        }
    }
}
