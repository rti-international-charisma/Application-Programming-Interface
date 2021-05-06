package com.rti.charisma.api.content.serialiser

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.rti.charisma.api.config.ACCESSIBILITY_STATUS
import com.rti.charisma.api.config.ConfigProvider
import com.rti.charisma.api.content.Page

object PageConversions {
    object Serializer : JsonSerializer<Page>() {
        override fun serialize(value: Page, gen: JsonGenerator, serializers: SerializerProvider) {
            with(gen) {
                if (canAccess(value.status)) {
                    writeStartObject()
                    writeStringField("title", value.title)
                    writeStringField("introduction", value.introduction)
                    writeStringField("description", value.description)
                    writeStringField("summary", value.summary)

                    if (value.heroImage != null) {
                        writeObjectFieldStart("heroImage")
                        writeStringField("title", value.heroImage?.title)
                        writeStringField("introduction", value.heroImage?.introduction)
                        writeStringField("summary", value.heroImage?.summary)
                        writeStringField("imageUrl", "/assets/${value.heroImage?.imageUrl}")
                        writeEndObject()

                    }

                    if (value.images != null) {
                        writeArrayFieldStart("images")
                        value.images.forEach { image ->
                            writeStartObject()
                            writeStringField("title", image.imageFile.title)
                            writeStringField("imageUrl", "/assets/${image.imageFile.imageUrl}")
                            writeEndObject()
                        }
                        writeEndArray()
                    }
                    if (value.videoSection != null) {
                        val videoSection = value.videoSection
                        writeObjectFieldStart("videoSection")
                        writeStringField("introduction", value.videoSection.introduction)
                        writeStringField("summary", value.videoSection.summary)
                        writeArrayFieldStart("videos")
                        videoSection.videos.forEach { video ->
                            writeStartObject()
                            writeStringField("title", video.title)
                            writeStringField("description", video.description)
                            writeStringField("videoUrl", if (video.videoUrl == null) "" else "/assets/${video.videoUrl}")
                            writeStringField("videoImage", if (video.videoImage == null) "" else "/assets/${video.videoImage}")
                            writeStringField("actionText", video.actionText)
                            writeStringField("actionLink", video.actionLink)
                            writeBooleanField("isPrivate", video.isPrivate)
                            writeEndObject()
                        }
                        writeEndArray()
                        writeEndObject()
                    }

                    if (value.steps != null) {
                        writeArrayFieldStart("steps")
                        value.steps.forEach { step ->
                            writeStartObject()
                            writeStringField("title", step.title)
                            writeStringField("subTitle", step.subTitle)
                            writeStringField("backgroundImageUrl", "/assets/${step.backgroundImageUrl}")
                            writeStringField("imageUrl", "/assets/${step.imageUrl}")
                            writeEndObject()
                        }
                        writeEndArray()
                    }
                    writeEndObject()
                } else {
                    writeStartObject()
                    writeEndObject()
                }
            }
        }
    }

    private fun canAccess(status: String): Boolean {
        val states: List<String> = ConfigProvider.getList(ACCESSIBILITY_STATUS)
        return states.contains(status.toLowerCase())
    }
}
