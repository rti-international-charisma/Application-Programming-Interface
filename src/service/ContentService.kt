package service

import com.rti.charisma.api.client.ContentClient
import com.rti.charisma.api.exception.ContentException
import com.rti.charisma.api.exception.ContentRequestException
import com.rti.charisma.api.model.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*

class ContentService(private val contentClient: ContentClient) {

    suspend fun getHomePage(): HomePage {

        val endpoint = "/items/homepage?fields=*.*.*"
        //supports 3 levels of information
        try {
            val content: HomePageContent = contentClient.getClient().request {
                url("${contentClient.baseUrl}${endpoint}")
                method = HttpMethod.Get
                header("Authorization", "Bearer ${contentClient.accessToken}")
            }
            return content.homepage
        } catch (e: ClientRequestException) {
            throw ContentRequestException("Failed to fetch content, ${e.message}}")
        } catch (e: ServerResponseException) {
            throw ContentException("Error while fetching content from server")
        } catch (e: Exception) {
            throw ContentException("Unexpected error while fetching content from server")
        }
    }

    suspend fun getPage(pageId: String): Page {
        val endpoint = "/items/pages/${pageId}?fields=*.*.*"
        //supports 3 levels of information
        try {
            val pageContent: PageContent = contentClient.getClient().request {
                url("${contentClient.baseUrl}${endpoint}")
                method = HttpMethod.Get
                header("Authorization", "Bearer ${contentClient.accessToken}")
            }
            return pageContent.page
        } catch (e: ClientRequestException) {
            throw ContentRequestException("Failed to fetch content, ${e.message}}")
        } catch (e: ServerResponseException) {
            throw ContentException("Error while fetching content from server")
        } catch (e: Exception) {
            throw ContentException("Unexpected error while fetching content from server")
        }
    }

    suspend fun getAssessment(): Assessment {
        val endpoint =
            "/items/sections?fields=*,questions.questions_id.text,questions.questions_id.options.options_id.*"
        try {
            return contentClient.getClient().request {
                url("${contentClient.baseUrl}${endpoint}")
                method = HttpMethod.Get
                header("Authorization", "Bearer ${contentClient.accessToken}")
            }
        } catch (e: ClientRequestException) {
            throw ContentRequestException("Failed to fetch content, ${e.message}}")
        } catch (e: ServerResponseException) {
            throw ContentException("Error while fetching content from server")
        } catch (e: Exception) {
            throw ContentException("Unexpected error while fetching content from server")
        }
    }

    suspend fun getAsset(assetID: String): ByteArray {
        try {
            return contentClient.requestAsset("/assets/${assetID}")
        } catch (e: Exception) {
            throw ContentException(e.localizedMessage)
        }
    }

}
