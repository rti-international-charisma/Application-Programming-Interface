package com.rti.charisma.api.route

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Routing.healthRoute() {
    get("/health") {
        call.respond(HttpStatusCode.OK, "OK")
    }
}