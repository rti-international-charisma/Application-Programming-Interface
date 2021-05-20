package com.rti.charisma.api.fixtures

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rti.charisma.api.content.Referral
import com.rti.charisma.api.content.Referrals

object ReferralsFixture {
    fun responseJson(): String {
        return """[ {
  "type" : "health",
  "name" : "Tara hospital",
  "addressAndContactInfo" : "50 Saxon Road, Hurlingham,\n011 535 3000",
  "imageUrl" : "/assets/5a28b210-1697-4cc0-8c42-4d17ad0d8198"
}, {
  "type" : "hotline",
  "name" : "Hotline",
  "addressAndContactInfo" : "Some address",
  "imageUrl" : null
}, {
  "type" : "shelter",
  "name" : "Shelter Children",
  "addressAndContactInfo" : "Some address\nCode 32432432, 23423423423",
  "imageUrl" : null
} ]"""

    }

    fun givenReferrals(): Referrals {
        val referral1 = Referral(
            "health",
            "Tara hospital",
            "50 Saxon Road, Hurlingham,\n011 535 3000",
            "5a28b210-1697-4cc0-8c42-4d17ad0d8198"
        )

        val referral2 = Referral(
            "hotline",
            "Hotline",
            "Some address",
            null
        )
        val referral3 = Referral(
            "shelter",
            "Shelter Children",
            "Some address\nCode 32432432, 23423423423",
            null
        )


        return Referrals(listOf(referral1, referral2, referral3))
    }

    fun cmsResponse(): Referrals {
        val content = """{ "data": [ {
  "type" : "health",
  "name" : "Tara hospital",
  "address_and_contact_info" : "50 Saxon Road, Hurlingham,\n011 535 3000",
  "image" : "5a28b210-1697-4cc0-8c42-4d17ad0d8198"
}, {
  "type" : "hotline",
  "name" : "Hotline",
  "address_and_contact_info" : "Some address",
  "image" : null
}, {
  "type" : "shelter",
  "name" : "Shelter Children",
  "address_and_contact_info" : "Some address\nCode 32432432, 23423423423",
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
  "address_and_contact_info" : "50 Saxon Road, Hurlingham,\n011 535 3000, 323423324234",
  "image" : "5a28b210-1697-4cc0-8c42-4d17ad0d8198"
}]}"""
        return jacksonObjectMapper().readValue(content, Referrals::class.java)

    }
}


