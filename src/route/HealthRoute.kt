package com.rti.charisma.api.route

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get

fun Routing.healthRoute() {
    get("/health") {
        call.respond(HttpStatusCode.OK, "OK")
    }
}