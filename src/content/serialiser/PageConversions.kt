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

                    value.heroImage?.let {
                        writeObjectFieldStart("heroImage")
                        writeStringField("title", it.title)
                        writeStringField("introduction", it.introduction)
                        writeStringField("summary", it.summary)
                        writeStringField("imageUrl", "/assets/${it.imageUrl}")
                        writeEndObject()
                    }
                    value.images?.let {
                        writeArrayFieldStart("images")
                        value.images.forEach { image ->
                            writeStartObject()
                            writeStringField("title", image.imageFile.title)
                            writeStringField("imageUrl", "/assets/${image.imageFile.imageUrl}")
                            writeEndObject()
                        }
                        writeEndArray()
                    }
                    value.videoSection?.let {
                        writeObjectFieldStart("videoSection")
                        writeStringField("introduction", it.introduction)
                        writeStringField("summary", it.summary)
                        writeArrayFieldStart("videos")
                        it.videos.forEach { video ->
                            writeStartObject()
                            writeStringField("title", video.title)
                            writeStringField("description", video.description)
                            writeStringField(
                                "videoUrl",
                                if (video.videoUrl == null) "" else "/assets/${video.videoUrl}"
                            )
                            writeStringField(
                                "videoImage",
                                if (video.videoImage == null) "" else "/assets/${video.videoImage}"
                            )
                            writeStringField("actionText", video.actionText)
                            writeStringField("actionLink", video.actionLink)
                            writeBooleanField("isPrivate", video.isPrivate)
                            writeEndObject()
                        }
                        writeEndArray()
                        writeEndObject()
                    }

                    value.steps?.let {
                        writeArrayFieldStart("steps")
                        it.forEach { step ->
                            writeStartObject()
                            writeStringField("title", step.title)
                            writeStringField("subTitle", step.subTitle)
                            writeStringField("backgroundImageUrl", "/assets/${step.backgroundImageUrl}")
                            writeStringField("imageUrl", "/assets/${step.imageUrl}")
                            writeEndObject()
                        }
                        writeEndArray()
                    }

                    value.counsellingModuleSections?.let {
                        writeArrayFieldStart("counsellingModuleSections")
                        it.forEach { section ->
                            writeStartObject()
                            writeStringField("id", section.id)
                            writeStringField("title", section.title)
                            writeStringField("introduction", section.introduction)
                            writeStringField("summary", section.summary)
                            section.accordionContent?.let { accordions ->
                                writeArrayFieldStart("accordionContent")
                                accordions.forEach { accordion ->
                                    writeStartObject()
                                    writeStringField("id", accordion.id)
                                    writeStringField("description", accordion.description)
                                    writeStringField("title", accordion.title)
                                    writeEndObject()
                                }
                                writeEndArray()
                            }
                            writeEndObject()
                        }
                        writeEndArray()
                    }
                    value.counsellingModuleActionPoints?.let {
                        writeArrayFieldStart("counsellingModuleActionPoints")
                        it.forEach { actionPoint ->
                            writeStartObject()
                            writeStringField("id", actionPoint.id)
                            writeStringField("title", actionPoint.title)
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
