package com.rti.charisma.api.content

import com.contentful.java.cda.CDAClient
import com.contentful.java.cda.CDAEntry
import com.rti.charisma.api.model.HomePage
import com.rti.charisma.api.model.converter

class ContentService {
    var client = CDAClient.builder()
        .setToken("c0JOePfprGTcMTvUcYT3pwvEtmKm0nY7sAV5G1Dq01Q")
        .setSpace("5lkmroeaw7nj")
        .build()

    //TODO: set locale

    fun getHomePage(): HomePage{
        val homePageEntry : CDAEntry = client.fetch(CDAEntry::class.java)
            .withContentType("homePage")
            .where("fields.pageid", "charisma-home")
            .include(5)
            .all()
            .items()
            .first() as CDAEntry
        return converter(homePageEntry);
    }
}