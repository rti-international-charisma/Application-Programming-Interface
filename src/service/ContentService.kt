package service

import com.contentful.java.cda.CDAClient
import com.contentful.java.cda.CDAEntry
import com.rti.charisma.api.model.HomePage
import com.rti.charisma.api.model.converter

class ContentService(private val client: CDAClient) {
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