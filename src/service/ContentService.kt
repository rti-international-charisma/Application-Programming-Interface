package service

import com.rti.charisma.api.client.CmsContent
import com.rti.charisma.api.client.ContentClient
import com.rti.charisma.api.config.ACCESSIBILITY_STATUS
import com.rti.charisma.api.config.ConfigProvider
import com.rti.charisma.api.exception.ContentException
import com.rti.charisma.api.exception.ContentRequestException
import com.rti.charisma.api.exception.NoContentAvailableException
import com.rti.charisma.api.model.HomePage
import com.rti.charisma.api.model.Page

class ContentService(private val contentClient: ContentClient) {

    suspend fun getHomePage(): HomePage {
        //supports 3 levels of information
        try {
            val content: CmsContent = contentClient.request("/items/homepage?fields=*.*.*")
            val status: String = (content.data["status"] ?: "") as String
            if (canAccess(status)) {
                return HomePage.toHomePage(content.data)
            } else {
                throw NoContentAvailableException("No content available")
            }
        } catch (e: ContentRequestException) {
            throw e
        } catch (e: ContentException) {
            throw e
        }
    }
    suspend fun getPage(pageId: String): Page {
        //supports 3 levels of information
        try {
            val content: CmsContent = contentClient.request("/items/pages/${pageId}?fields=*.*.*")
            val status: String = (content.data["status"] ?: "") as String
            if (canAccess(status)) {
                return Page.toPage(content.data)
            } else {
                throw NoContentAvailableException("No content available")
            }
        } catch (e: ContentRequestException) {
            throw e
        } catch (e: ContentException) {
            throw e
        }
    }


    private fun canAccess(status: String): Boolean {
        val states : List<String> = ConfigProvider.getList(ACCESSIBILITY_STATUS)
        return states.contains(status)
    }
}
