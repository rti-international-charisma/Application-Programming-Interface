package com.rti.charisma.api.content.serialiser

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rti.charisma.api.content.Referral
import com.rti.charisma.api.content.Referrals
import com.rti.charisma.api.fixtures.ReferralsFixture
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ReferralsConversionsTest {
    @Test
    fun `it should serialise referrals`() {

        val json = jacksonObjectMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(ReferralsFixture.givenReferrals())

        assertEquals(ReferralsFixture.responseJson(), json)
    }

    @Test
    fun `it should serialise a referral`() {

        val referral = Referral(
            "type",
            "name",
            "324234234",
            "address 1, address 2, code - 32423",
            "image-id"
        )
        val json = jacksonObjectMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(Referrals(listOf(referral)))

        val singleReferral = """[ {
  "type" : "type",
  "name" : "name",
  "address" : "address 1, address 2, code - 32423",
  "contact" : "324234234",
  "imageUrl" : "/assets/image-id"
} ]"""
        assertEquals(singleReferral, json)
    }

    @Test
    fun `it should ignore empty values`() {

        val referral = Referral(
            "type",
            "name",
            "",
            "",
            ""
        )
        val json = jacksonObjectMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(Referrals(listOf(referral)))

        val singleReferral = """[ {
  "type" : "type",
  "name" : "name",
  "address" : "",
  "contact" : "",
  "imageUrl" : null
} ]"""
        assertEquals(singleReferral, json)
    }


    @Test
    fun `it should ignore optional values`() {

        val referral = Referral(
            "type",
            "name",
            null,
            null,
            null
        )
        val json = jacksonObjectMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(Referrals(listOf(referral)))

        val singleReferral = """[ {
  "type" : "type",
  "name" : "name",
  "address" : null,
  "contact" : null,
  "imageUrl" : null
} ]"""
        assertEquals(singleReferral, json)
    }


}