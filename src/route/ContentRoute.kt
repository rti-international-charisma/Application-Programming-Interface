package com.rti.charisma.api.route

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import service.ContentService


@KtorExperimentalLocationsAPI
fun Routing.contentRoute(contentService: ContentService) {
    get("/home") {
        val homePage = contentService.getHomePage()
        call.respond(homePage)
    }

    get("/assessment/intro") {
        val introPage = contentService.getPage("assessment-intro")
        call.respond(introPage)
    }

    get("/aboutus") {
        val introPage = contentService.getPage("aboutus")
        call.respond(introPage)
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

}

enum class CONSENT(val value: String) {
    AGREE("agree"),
    NEUTRAL("neutral"),
    OPPOSE("oppose"),
    UNAWARE("unaware")
}

