package com.rti.charisma.api.route

import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import service.ContentService

//@JsonIgnoreProperties(ignoreUnknown = true)
//data class HomePage(val data: MutableMap<String, Any>)

//TODO


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

    get("/assessment") {
        val assessment = contentService.getAssessment()
        call.respond(assessment)
    }


    get("/assets/{assetID}") {
        val asset = contentService.getAsset("${call.parameters["assetID"]}")
        call.respondBytes(asset)
    }
}



