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

    fun responseJsonForReferralFilter(): String {
        return """[ {
  "type" : "Counselling",
  "name" : "Lifeline/Victim Empowerment",
  "addressAndContactInfo" : "Booysens\nSgt. Mothibi 011 433 5386 \nSinenhlanhla (social worker) \nsinenhlanhla@lifelinejhb.org.za 011 728 1331",
  "imageUrl" : "/assets/65de9bf3-50a7-4d06-a410-a7aed7fbb3ac"
}, {
  "type" : "Counselling",
  "name" : "SANCA",
  "addressAndContactInfo" : "Some address",
  "imageUrl" : null
}, {
  "type" : "Counselling",
  "name" : "Sophiatown Counseling",
  "addressAndContactInfo" : "Some address\nCode 32432432, 23423423423",
  "imageUrl" : null
} ]"""

    }

    fun givenReferralsForReferralFilter(): Referrals {
        val referral1 = Referral(
            "Counselling",
            "Lifeline/Victim Empowerment",
            "Booysens\nSgt. Mothibi 011 433 5386 \nSinenhlanhla (social worker) \nsinenhlanhla@lifelinejhb.org.za 011 728 1331",
            "65de9bf3-50a7-4d06-a410-a7aed7fbb3ac"
        )

        val referral2 = Referral(
            "Counselling",
            "SANCA",
            "Some address",
            null
        )
        val referral3 = Referral(
            "Counselling",
            "Sophiatown Counseling",
            "Some address\nCode 32432432, 23423423423",
            null
        )


        return Referrals(listOf(referral1, referral2, referral3))
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

    fun cmsResponseWithOneReferralType(): Referrals {
        val content = """{ "data": [ {
  "type" : "Counselling",
  "name" : "Sophiatown Counseling",
  "address_and_contact_info" : "Some address Code 32432432, 23423423423",
  "image" : "5a28b210-1697-4cc0-8c42-4d17ad0d8198"
}]}"""
        return jacksonObjectMapper().readValue(content, Referrals::class.java)

    }
}


