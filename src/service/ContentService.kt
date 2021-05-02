package service

import com.rti.charisma.api.client.ContentClient
import com.rti.charisma.api.content.Assessment
import com.rti.charisma.api.content.Page
import com.rti.charisma.api.content.PageContent
import com.rti.charisma.api.exception.ContentException
import com.rti.charisma.api.exception.ContentRequestException
import com.rti.charisma.api.route.CONSENT

const val PREP_ABUSE: String = "prep-abuse"
const val PREP_NEUTRAL: String = "prep-neutral"
const val PREP_AGREE: String = "prep-agree"
const val PREP_OPPOSE: String = "prep-oppose"
const val PREP_UNAWARE: String = "prep-unaware"

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

    suspend fun getModule(partnerScore: Int, consent: CONSENT): Page {
        val moduleId: String = selectModuleId(partnerScore, consent)
        val endpoint = "/items/counselling_module/${moduleId}?fields=*.*,*.accordion_content.*"
        return pageRequest(endpoint)
    }

    private fun selectModuleId(partnerScore: Int, consent: CONSENT): String {
        when {
            (partnerScore in 13..42) -> return PREP_ABUSE
            (partnerScore in 1..12 && CONSENT.AGREE == (consent)) -> return PREP_AGREE
            (partnerScore in 1..12 && CONSENT.NEUTRAL == (consent)) -> return PREP_NEUTRAL
            (partnerScore in 1..12 && CONSENT.OPPOSE == (consent)) -> return PREP_OPPOSE
            (partnerScore in 1..12 && CONSENT.UNAWARE == (consent)) -> return PREP_UNAWARE

        }
        throw ContentException("Failed to recommend module")
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
        } catch (e: ContentRequestException) {
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

