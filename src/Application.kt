package com.rti.charisma.api

import com.contentful.java.cda.CDAClient
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.rti.charisma.api.db.CharismaDB
import com.viartemev.ktor.flyway.FlywayFeature
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.date.*
import org.slf4j.event.Level
import service.ContentService


fun main() {
    embeddedServer(Netty, port = 5000) {
        main()
    }.start(wait = true)
}
@kotlin.jvm.JvmOverloads
fun Application.main() {
    val contentClient = CDAClient.builder()
        .setToken("c0JOePfprGTcMTvUcYT3pwvEtmKm0nY7sAV5G1Dq01Q")
        .setSpace("5lkmroeaw7nj")
        .build()

    val contentService = ContentService(contentClient);

    mainWithDependencies(contentClient, contentService)

}

fun Application.mainWithDependencies(contentClient: CDAClient, contentService: ContentService) {
    initDB()
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(CachingHeaders) {
        options { outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.CSS -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60), expires = null as? GMTDate?)
                else -> null
            }
        }
    }
    install(ContentNegotiation) {
        jackson {
            configure(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT, true)
            setDefaultPrettyPrinter(DefaultPrettyPrinter())
            registerModule(JavaTimeModule())  // support java.time.* types
        }
    }
    install(StatusPages) {
        exception<AuthenticationException> {
            call.respond(HttpStatusCode.Unauthorized)
        }
        exception<AuthorizationException> {
            call.respond(HttpStatusCode.Forbidden)
        }
        exception<Throwable> {
            call.respond(HttpStatusCode.InternalServerError)
        }

    }

    install(CORS) {
        anyHost()
        allowCredentials = true
        header(HttpHeaders.AccessControlAllowOrigin)
    }

    install(FlywayFeature) {
        dataSource = CharismaDB.getDataSource()
    }

    routing {
        defaultRoute()
        healthCheckRoute(contentClient)
        contentRoute(contentService)
    }
}

fun initDB() {
    CharismaDB.init()
}

private fun Routing.contentRoute(contentService: ContentService) {
    get (path = "/content"){
        call.respond(contentService.getHomePage());
    }
}

fun Routing.healthCheckRoute(contentClient: CDAClient) {
    get("/health") {
        //check cms connection
        //check db connection
        call.respondText("OK", contentType = io.ktor.http.ContentType.Application.Json)
    }

}fun Routing.defaultRoute() {
    get("/") {
        call.respondText("Try /content", contentType = io.ktor.http.ContentType.Application.Json)
    }

}

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()

