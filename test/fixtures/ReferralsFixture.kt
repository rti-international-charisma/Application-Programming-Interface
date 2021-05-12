package com.rti.charisma.api.fixtures

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rti.charisma.api.content.Referral
import com.rti.charisma.api.content.Referrals

object ReferralsFixture {
    fun responseJson(): String {
        return """[ {
  "type" : "health",
  "name" : "Tara hospital",
  "address" : "50 Saxon Road, Hurlingham,\n011 535 3000",
  "contact" : "",
  "imageUrl" : "/assets/5a28b210-1697-4cc0-8c42-4d17ad0d8198"
}, {
  "type" : "hotline",
  "name" : "Hotline",
  "address" : null,
  "contact" : "9747347534",
  "imageUrl" : null
}, {
  "type" : "shelter",
  "name" : "Shelter Children",
  "address" : "Some adrress\nCode 32432432",
  "contact" : "23423423423",
  "imageUrl" : null
} ]"""

    }

    fun givenReferrals(): Referrals {
        val referral1 = Referral(
            "health",
            "Tara hospital",
            "",
            "50 Saxon Road, Hurlingham,\n011 535 3000",
            "5a28b210-1697-4cc0-8c42-4d17ad0d8198"
        )

        val referral2 = Referral(
            "hotline",
            "Hotline",
            "9747347534",
            null,
            null
        )
        val referral3 = Referral(
            "shelter",
            "Shelter Children",
            "23423423423",
            "Some adrress\nCode 32432432",
            null
        )


        return Referrals(listOf(referral1, referral2, referral3))
    }

    fun cmsResponse(): Referrals {
        val content = """{ "data": [ {
  "type" : "health",
  "name" : "Tara hospital",
  "address" : "50 Saxon Road, Hurlingham,\n011 535 3000",
  "contact_number" : "",
  "image" : "5a28b210-1697-4cc0-8c42-4d17ad0d8198"
}, {
  "type" : "hotline",
  "name" : "Hotline",
  "address" : null,
  "contact_number" : "9747347534",
  "image" : null
}, {
  "type" : "shelter",
  "name" : "Shelter Children",
  "address" : "Some adrress\nCode 32432432",
  "contact_number" : "23423423423",
  "image" : null
} ]}"""
        return jacksonObjectMapper().readValue(content, Referrals::class.java)
    }

    fun noReferrals(): Referrals {
        return Referrals(emptyList())
    }

    fun cmsResponseWithOneReferral(): Referrals {
        val content = """{ "data": [ {
  "type" : "health",
  "name" : "Tara hospital",
  "address" : "50 Saxon Road, Hurlingham,\n011 535 3000",
  "contact_number" : "323423324234",
  "image" : "5a28b210-1697-4cc0-8c42-4d17ad0d8198"
}]}"""
        return jacksonObjectMapper().readValue(content, Referrals::class.java)

    }
}


