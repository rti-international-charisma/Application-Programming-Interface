package com.rti.charisma.api.service

import com.rti.charisma.api.client.ContentClient
import com.rti.charisma.api.config.ConfigProvider
import com.rti.charisma.api.content.Assessment
import com.rti.charisma.api.content.Page
import com.rti.charisma.api.content.PageContent
import com.rti.charisma.api.content.Referrals
import com.rti.charisma.api.exception.ContentRequestException
import com.rti.charisma.api.exception.ContentServerException
import com.rti.charisma.api.route.CONSENT
import org.slf4j.LoggerFactory


object PrePModules {
    const val PREP_ABUSE: String = "ktor.application.prep_modules.prep_abuse"
    const val PREP_NEUTRAL: String = "ktor.application.prep_modules.prep_neutral"
    const val PREP_AGREE: String = "ktor.application.prep_modules.prep_agree"
    const val PREP_OPPOSE: String = "ktor.application.prep_modules.prep_oppose"
    const val PREP_UNAWARE: String = "ktor.application.prep_modules.prep_unaware"

    fun getModuleId(key: String): String {
        return (ConfigProvider.get(key))
    }
}

class ContentService(private val contentClient: ContentClient) {
    private val logger = LoggerFactory.getLogger(ContentService::class.java)

    suspend fun getHomePage(): Page {
        // supports 3 levels of information
        val endpoint = "/items/homepage?fields=*.*.*"
        return pageRequest(endpoint)
    }

    suspend fun getPage(pageId: String): Page {
        // supports 3 levels of information
        val endpoint = "/items/pages/$pageId?fields=*.*.*"
        return pageRequest(endpoint)
    }

    suspend fun getModule(partnerScore: Int, consent: CONSENT): Page {
        val moduleId: String = selectModuleId(partnerScore, consent)
        return getModule(moduleId)
    }

    suspend fun getModule(moduleId: String): Page {
        val endpoint = "/items/counselling_module/$moduleId?fields=*.*,*.accordion_content.*"
        return pageRequest(endpoint)
    }

    suspend fun getAssessments(): Assessment {
        val endpoint =
            "/items/sections?sort=sort&fields=*,questions.questions_id.*,questions.questions_id.options.options_id.*"
        try {
            val assessment = contentClient.getAssessment(endpoint)
            logger.info("Assessment content received successfully")
            return assessment
        } catch (e: ContentRequestException) {
            logger.warn("Request failed for assessment, ${e.localizedMessage}")
            throw ContentRequestException(e.localizedMessage)
        } catch (e: ContentServerException) {
            logger.warn("Request failed for assessment, ${e.localizedMessage}")
            throw ContentServerException(e.localizedMessage, e)
        }
    }

    suspend fun getReferrals(): Referrals {
        val endpoint = "/items/referrals"
        try {
            val referrals = contentClient.getReferrals(endpoint)
            logger.info("Referrals received successfully")
            return referrals
        } catch (e: ContentRequestException) {
            logger.warn("Request failed for referrals, ${e.localizedMessage}")
            throw ContentRequestException(e.localizedMessage)
        } catch (e: ContentServerException) {
            logger.warn("Request failed for referrals, ${e.localizedMessage}")
            throw ContentServerException(e.localizedMessage, e)
        }
    }

    @Deprecated("To be removed")
    suspend fun getAsset(assetId: String): ByteArray {
        try {
            return contentClient.getAsset("/assets/$assetId")
        } catch (e: ContentRequestException) {
            logger.warn("Request failed for asset, $assetId, ${e.localizedMessage}")
            throw ContentRequestException(e.localizedMessage)
        } catch (e: ContentServerException) {
            throw ContentServerException(e.localizedMessage, e)
        }
    }

    private fun selectModuleId(partnerScore: Int, consent: CONSENT): String {
        return when {
            (partnerScore in 13..42) -> return PrePModules.getModuleId(PrePModules.PREP_ABUSE)
            (partnerScore in 1..12 && CONSENT.AGREE == (consent)) -> PrePModules.getModuleId(PrePModules.PREP_AGREE)
            (partnerScore in 1..12 && CONSENT.NEUTRAL == (consent)) -> PrePModules.getModuleId(PrePModules.PREP_NEUTRAL)
            (partnerScore in 1..12 && CONSENT.OPPOSE == (consent)) -> PrePModules.getModuleId(PrePModules.PREP_OPPOSE)
            (partnerScore in 1..12 && CONSENT.UNAWARE == (consent)) -> PrePModules.getModuleId(PrePModules.PREP_UNAWARE)
            else -> {
                logger.warn("Failed to recommend module for score, $partnerScore and consent, $consent")
                throw ContentRequestException("Failed to recommend module for score, $partnerScore and consent, $consent")
            }
        }
    }

    private suspend fun pageRequest(endpoint: String): Page {
        logger.info("Sending request to fetch content, $endpoint")
        try {
            val content: PageContent = contentClient.getPage(endpoint)
            logger.info("Retrieved content successfully for $endpoint")
            return content.page
        } catch (e: ContentRequestException) {
            logger.warn("Content request failed for $endpoint, ${e.localizedMessage}")
            throw ContentRequestException(e.localizedMessage)
        } catch (e: ContentServerException) {
            logger.warn("Failed to get content from server for $endpoint, ${e.localizedMessage}")
            throw ContentServerException(e.localizedMessage, e)
        }
    }
}
