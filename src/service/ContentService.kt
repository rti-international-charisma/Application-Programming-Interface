package service

import com.rti.charisma.api.client.ContentClient
import com.rti.charisma.api.config.ConfigProvider
import com.rti.charisma.api.content.Assessment
import com.rti.charisma.api.content.Page
import com.rti.charisma.api.content.PageContent
import com.rti.charisma.api.exception.ContentException
import com.rti.charisma.api.exception.ContentRequestException
import com.rti.charisma.api.route.CONSENT


object PrePModules {
    const val PREP_ABUSE: String = "ktor.application.prep_modules.prep-abuse"
    const val PREP_NEUTRAL: String = "ktor.application.prep_modules.prep-neutral"
    const val PREP_AGREE: String = "ktor.application.prep_modules.prep-agree"
    const val PREP_OPPOSE: String = "ktor.application.prep_modules.prep-oppose"
    const val PREP_UNAWARE: String = "ktor.application.prep_modules.prep-unaware"

    fun getModuleId(key: String): String {
        return (ConfigProvider.get(key))
    }
}

class ContentService(private val contentClient: ContentClient) {

    suspend fun getHomePage(): Page {
        //supports 3 levels of information
        val endpoint = "/items/homepage?fields=*.*.*"
        return pageRequest(endpoint)
    }

    suspend fun getPage(pageId: String): Page {
        //supports 3 levels of information
        val endpoint = "/items/pages/${pageId}?fields=*.*.*"
        return pageRequest(endpoint)
    }

    suspend fun getModule(partnerScore: Int, consent: CONSENT): Page {
        val moduleId: String = selectModuleId(partnerScore, consent)
        val endpoint = "/items/counselling_module/${moduleId}?fields=*.*,*.accordion_content.*"
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
        } catch (e: ContentRequestException) {
            throw ContentRequestException(e.localizedMessage)
        } catch (e: Exception) {
            throw ContentException(e.localizedMessage)
        }
    }

    private fun selectModuleId(partnerScore: Int, consent: CONSENT): String {
        when {
            (partnerScore in 13..42) -> return PrePModules.getModuleId(PrePModules.PREP_ABUSE)
            (partnerScore in 1..12 && CONSENT.AGREE == (consent)) -> return PrePModules.getModuleId(PrePModules.PREP_AGREE)
            (partnerScore in 1..12 && CONSENT.NEUTRAL == (consent)) -> return PrePModules.getModuleId(PrePModules.PREP_NEUTRAL)
            (partnerScore in 1..12 && CONSENT.OPPOSE == (consent)) -> return PrePModules.getModuleId(PrePModules.PREP_OPPOSE)
            (partnerScore in 1..12 && CONSENT.UNAWARE == (consent)) -> return PrePModules.getModuleId(PrePModules.PREP_UNAWARE)

        }
        throw ContentException("Failed to recommend module for score, $partnerScore and consent, $consent")
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

