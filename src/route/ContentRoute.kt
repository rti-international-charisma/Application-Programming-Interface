package com.rti.charisma.api.route

import com.rti.charisma.api.service.ContentService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*


@KtorExperimentalLocationsAPI
fun Routing.contentRoute(contentService: ContentService) {

    get("/home") {
        val homePage = contentService.getHomePage()
        call.respond(homePage)
    }
    get("/assessment") {
        val assessment = contentService.getAssessment()
        call.respond(assessment)
    }

    get("assessment/module") {
        //score=<score>&prep=<disagree|opposed|neutral|agree>
        val partnerScore = call.parameters["partner_score"]
        val prepConsent = call.parameters["prep_consent"]
        if (partnerScore.isNullOrEmpty() || prepConsent.isNullOrEmpty()) {
            call.respond(HttpStatusCode.BadRequest, "Missing required query parameters")
        } else {
            val assessmentScores = contentService.getModule(partnerScore.toInt(), CONSENT.valueOf(prepConsent.toUpperCase()))
            call.respond(assessmentScores)
        }

    }

    get("/assets/{assetID}") {
        val asset = contentService.getAsset("${call.parameters["assetID"]}")
        call.respondBytes(asset)
    }

    get("/assessment/intro") {
       getPage("assessment-intro", contentService)

    }

    get("/aboutus") {
        getPage("aboutus", contentService)
    }

}

private suspend fun PipelineContext<Unit, ApplicationCall>.getPage(pageId: String, contentService: ContentService) {
    pageId.let { call.respond(contentService.getPage(pageId)) }
}

enum class CONSENT(val value: String) {
    AGREE("agree"),
    NEUTRAL("neutral"),
    OPPOSE("oppose"),
    UNAWARE("unaware")
}

