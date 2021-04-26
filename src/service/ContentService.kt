package service

import com.rti.charisma.api.client.ContentClient
import com.rti.charisma.api.exception.ContentException
import com.rti.charisma.api.exception.ContentRequestException
import com.rti.charisma.api.model.Assessment
import com.rti.charisma.api.model.Page
import com.rti.charisma.api.model.PageContent

class ContentService(private val contentClient: ContentClient) {

    suspend fun getHomePage(): Page {

        val endpoint = "/items/homepage?fields=*.*.*"
        //supports 3 levels of information
        return pageRequest(endpoint)
    }

    suspend fun getPage(pageId: String): Page {
        val endpoint = "/items/pages/${pageId}?fields=*.*.*"
        //supports 3 levels of information
        return pageRequest(endpoint)
    }

    suspend fun getAssessment(): Assessment {
        val endpoint =
            "/items/sections?sort=sort&fields=*,questions.questions_id.*,questions.questions_id.options.options_id.*"
        try {
            return contentClient.getAssessment(endpoint)
        } catch (e: ContentRequestException) {
            throw ContentRequestException(e.localizedMessage)
        } catch (e: Exception) {
            throw ContentException(e.localizedMessage)
        }
    }

    suspend fun getAsset(assetId: String): ByteArray {
        try {
            return contentClient.getAsset("/assets/${assetId}")
        }catch (e: ContentRequestException) {
            throw ContentRequestException(e.localizedMessage)
        } catch (e: Exception) {
            throw ContentException(e.localizedMessage)
        }
    }
    private suspend fun pageRequest(endpoint: String): Page {
        try {
            val content: PageContent = contentClient.getPage(endpoint)
            return content.page
        } catch (e: ContentRequestException) {
            throw ContentRequestException(e.localizedMessage)
        } catch (e: Exception) {
            throw ContentException(e.localizedMessage)
        }
    }
}
