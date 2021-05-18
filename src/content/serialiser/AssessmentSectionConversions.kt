package com.rti.charisma.api.content.serialiser

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.rti.charisma.api.config.ConfigProvider
import com.rti.charisma.api.config.IS_DRAFT_MODE
import com.rti.charisma.api.content.AssessmentSection

object AssessmentSectionConversions {
    object Serializer : JsonSerializer<AssessmentSection>() {
        override fun serialize(value: AssessmentSection, gen: JsonGenerator, serializers: SerializerProvider) {
            with(gen) {
                if (canAccess(value.status)) {
                    writeStartObject()
                    writeStringField("id", value.id)
                    writeStringField("section", value.section)
                    writeStringField("introduction", value.introduction)
                    writeArrayFieldStart("questions")
                    value.questions.forEach { question ->
                        writeStartObject()
                        writeStringField("id", question.id)
                        writeStringField("text", question.text)
                        writeStringField("description", question.description)
                        writeBooleanField("positiveNarrative", question.positiveNarrative)
                        writeArrayFieldStart("options")
                        question.options.forEach { option ->
                            writeStartObject()
                            writeStringField("text", option.text)
                            writeNumberField("weightage", option.weightage)
                            writeEndObject()
                        }
                        writeEndArray()
                        writeEndObject()
                    }
                    writeEndArray()
                    writeEndObject()
                }
            }
        }
    }

    private fun canAccess(status: String): Boolean {
        val isDraftMode: Boolean = ConfigProvider.get(IS_DRAFT_MODE).toBoolean()
        if (status.equals("PUBLISHED", true)) return true
        if (status.equals("DRAFT", true) && isDraftMode) return true
        return false;
    }
}
