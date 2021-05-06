package service

import com.rti.charisma.api.client.ContentClient
import com.rti.charisma.api.exception.ContentException
import com.rti.charisma.api.exception.ContentRequestException
import com.rti.charisma.api.content.Assessment
import com.rti.charisma.api.content.Page
import com.rti.charisma.api.content.PageContent
import org.slf4j.LoggerFactory

class ContentService(private val contentClient: ContentClient) {
    private val logger = LoggerFactory.getLogger(ContentService::class.java)

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
            throw ContentException(e.localizedMessage, e)
        }
    }

    suspend fun getAsset(assetId: String): ByteArray {
        try {
            return contentClient.getAsset("/assets/${assetId}")
        }catch (e: ContentRequestException) {
            throw ContentRequestException(e.localizedMessage)
        } catch (e: Exception) {
            throw ContentException(e.localizedMessage, e)
        }
    }
    private suspend fun pageRequest(endpoint: String): Page {
        logger.info("Ready to fetch content, $endpoint")
        try {
            val content: PageContent = contentClient.getPage(endpoint)
            logger.info("Retrieved content, $content")
            return content.page
        } catch (e: ContentRequestException) {
            logger.warn("Content request failed, ${e.localizedMessage}")
            throw ContentRequestException(e.localizedMessage)
        } catch (e: Exception) {
            logger.warn("Failed to get content from server, ${e.localizedMessage}")
            throw ContentException(e.localizedMessage, e)
        }
    }
}
