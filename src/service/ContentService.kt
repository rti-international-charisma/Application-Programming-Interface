package com.rti.charisma.api.content

import com.contentful.java.cda.CDAClient
import com.contentful.java.cda.CDAEntry
import com.rti.charisma.api.model.HomePage
import java.util.*

class ContentService {
    var client = CDAClient.builder()
        .setToken("c0JOePfprGTcMTvUcYT3pwvEtmKm0nY7sAV5G1Dq01Q")
        .setSpace("5lkmroeaw7nj")
        .build()

    fun getHomePage(): HomePage {


//        val homePage : HomePage = client.observeAndTransform(HomePage::class.java)
//            .where("fields.pageid", "charisma-home")
//            .include(5)
//            .limit(1)
//            .all()
//            .blockingFirst()
//            .first()
//        return homePage

        val homePage : CDAEntry = client.fetch(CDAEntry::class.java)
            .withContentType("homePage")
            .where("fields.pageid", "charisma-home")
            .include(5)
            .all()
            .items()
            .first() as CDAEntry


        return HomePage(
            homePage.getField("title") as String,
            homePage.getField("contentBody") as String,
            Collections.emptyList(),
            Collections.emptyList())
    }
}