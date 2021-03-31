package com.rti.charisma.api.route

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import service.ContentService

@JsonIgnoreProperties(ignoreUnknown = true)
data class HomePage(val data: MutableMap<String, Any>)

//TODO
//data class HomePage(val title: String, val description: String, val introduction: String, val heroImage: PageImage, val images: List<PageImage>, val videos: List<PageVideo>)
//@JsonIgnoreProperties(ignoreUnknown = true)
//data class PageImage(val title: String, val description: String, val summary: String, val id: String)
//@JsonIgnoreProperties(ignoreUnknown = true)
//data class PageVideo(val title: String, val description: String, val summary: String, val id: String, val actionButtonText: String)


@KtorExperimentalLocationsAPI
fun Routing.contentRoute(contentService: ContentService) {
    get("/homepage") {
        val homePage = contentService.getHomePage()
        call.respond(homePage)
    }
}