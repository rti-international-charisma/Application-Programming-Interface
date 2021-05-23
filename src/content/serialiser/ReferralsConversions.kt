package com.rti.charisma.api.content.serialiser

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.rti.charisma.api.content.Referrals

object ReferralsConversions {
    object Serializer : JsonSerializer<Referrals>() {
        override fun serialize(value: Referrals, gen: JsonGenerator, serializers: SerializerProvider) {
            with(gen) {
                writeStartArray()
                value.referrals.forEach { referral ->
                    writeStartObject()
                    writeStringField("type", referral.type)
                    writeStringField("name", referral.name)
                    writeStringField("addressAndContactInfo", referral.addressAndContactInfo)
                    writeStringField("imageUrl", ifPresent(referral.imageUrl)?.let {  "/assets/${referral.imageUrl}"})
                    writeEndObject()
                }
                writeEndArray()
            }
        }
    }

    private fun ifPresent(value: String?): String? {
        if (value?.isNotEmpty() == true) {
            return value
        }
        return null
    }
}
