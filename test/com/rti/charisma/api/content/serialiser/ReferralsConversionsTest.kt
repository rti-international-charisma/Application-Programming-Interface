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

        assertEquals(ReferralsFixture.responseJson(), json.replace("\r", ""))
    }

    @Test
    fun `it should serialise a referral`() {

        val referral = Referral(
            "type",
            "name",
            "address 1, address 2, code - 32423, 324234234",
            "image-id"
        )
        val json = jacksonObjectMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(Referrals(listOf(referral)))

        val singleReferral = """[ {
  "type" : "type",
  "name" : "name",
  "addressAndContactInfo" : "address 1, address 2, code - 32423, 324234234",
  "imageUrl" : "/assets/image-id"
} ]"""
        assertEquals(singleReferral, json?.replace("\r", ""))
    }

    @Test
    fun `it should ignore empty values`() {

        val referral = Referral(
            "type",
            "name",
            "",
            ""
        )
        val json = jacksonObjectMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(Referrals(listOf(referral)))

        val singleReferral = """[ {
  "type" : "type",
  "name" : "name",
  "addressAndContactInfo" : "",
  "imageUrl" : null
} ]"""
        assertEquals(singleReferral, json?.replace("\r", ""))
    }


    @Test
    fun `it should ignore optional values`() {

        val referral = Referral(
            "type",
            "name",
            null,
            null
        )
        val json = jacksonObjectMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(Referrals(listOf(referral)))

        val singleReferral = """[ {
  "type" : "type",
  "name" : "name",
  "addressAndContactInfo" : null,
  "imageUrl" : null
} ]"""
        assertEquals(singleReferral, json?.replace("\r", ""))
    }


}