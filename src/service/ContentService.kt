package service

import com.contentful.java.cda.CDAClient
import com.contentful.java.cda.CDAEntry
import com.rti.charisma.api.model.HomePage

class ContentService(private val client: CDAClient) {
    //TODO: set locale

    fun getHomePage(): HomePage{
        var homePageEntry: CDAEntry = CDAEntry()
        try{
            homePageEntry = client.fetch(CDAEntry::class.java)
               .withContentType("homePage")
               .where("fields.pageid", "charisma-home")
               .include(5)
               .all()
               .items()
               .first() as CDAEntry
       } catch (exception : Exception) {
            throw RuntimeException("Error fetching data from CMS ${exception.localizedMessage}")
       }
        return HomePage.converter(homePageEntry);
    }
}