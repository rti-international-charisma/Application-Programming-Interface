package com.rti.charisma.api.content.serialiser

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.rti.charisma.api.config.ConfigProvider
import com.rti.charisma.api.config.IS_DRAFT_MODE
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
                    writeStringField("heroImageCaptionTestComplete", value.heroImageCaptionTestComplete)
                    writeStringField("heroImageCaptionTestIncomplete", value.heroImageCaptionTestIncomplete)

                    value.heroImage?.let { image ->
                        writeObjectFieldStart("heroImage")
                        writeStringField("title", image.title)
                        writeStringField("introduction", image.introduction)
                        writeStringField("imageUrl", ifPresent(image.imageUrl)?.let { "/assets/${image.imageUrl}" })
                        writeEndObject()
                    }

                    value.moduleImage?.let {
                        writeObjectFieldStart("moduleImage")
                        writeStringField("imageUrl", ifPresent(it.moduleImage)?.let { value -> "/assets/$value" })
                        writeEndObject()
                    }

                    value.images?.let {
                        writeArrayFieldStart("images")
                        value.images.forEach { image ->
                            writeStartObject()
                            writeStringField("title", image.imageFile.title)
                            writeStringField(
                                "imageUrl",
                                ifPresent(image.imageFile.imageUrl)?.let { "/assets/${image.imageFile.imageUrl}" }
                            )
                            writeEndObject()
                        }
                        writeEndArray()
                    }

                    value.documents?.let {
                        writeArrayFieldStart("documents")
                        value.documents.forEach { document ->
                            writeStartObject()
                            writeStringField("title", document.document.title)
                            writeStringField("documentUrl", ifPresent(document.document.documentUrl)?.let { "/assets/${document.document.documentUrl}" })
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
                            writeStringField("videoUrl", ifPresent(video.videoUrl)?.let { "/assets/${video.videoUrl}" })
                            writeStringField(
                                "videoImage",
                                ifPresent(video.videoImage)?.let { "/assets/${video.videoImage}" }
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
                            writeNumberField("stepNumber", step.stepNumber)
                            writeStringField(
                                "backgroundImageUrl",
                                ifPresent(step.backgroundImageUrl)?.let { "/assets/${step.backgroundImageUrl}" }
                            )
                            writeStringField("imageUrl", ifPresent(step.imageUrl)?.let { "/assets/${step.imageUrl}" })
                            writeEndObject()
                        }
                        writeEndArray()
                    }

                    value.counsellingSections?.let {
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
                                    writeStringField("title", accordion.title)
                                    writeStringField("description", accordion.description)
                                    writeStringField(
                                        "imageUrl",
                                        ifPresent(accordion.imageUrl)?.let { "/assets/${accordion.imageUrl}" }
                                    )
                                    writeEndObject()
                                }
                                writeEndArray()
                            }
                            writeEndObject()
                        }
                        writeEndArray()
                    }
                    value.counsellingActionPoints?.let {
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

    private fun ifPresent(value: String?): String? {
        if (value?.isNotEmpty() == true) {
            return value
        }
        return null
    }

    private fun canAccess(status: String): Boolean {
        val isDraftMode: Boolean = ConfigProvider.get(IS_DRAFT_MODE).toBoolean()
        if (status.equals("PUBLISHED", true)) return true
        if (status.equals("DRAFT", true) && isDraftMode) return true
        return false
    }
}
