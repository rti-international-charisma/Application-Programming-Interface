package com.rti.charisma.api

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.google.gson.Gson
import com.rti.charisma.api.content.ContentService
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.date.*
import org.slf4j.event.Level


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    val contentService = ContentService();


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
        install(ContentNegotiation) {
            jackson {
                configure(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT, true)
                setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
                    indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
                    indentObjectsWith(DefaultIndenter("  ", "\n"))
                })
                registerModule(JavaTimeModule())  // support java.time.* types
            }
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

    routing {
        route("/") {

            get() {
                call.respondText("Welcome", contentType = io.ktor.http.ContentType.Text.Html)
            }
            get("/health_check") {
                //check cms connection
                //check db connection
                call.respondText("OK", contentType = io.ktor.http.ContentType.Application.Json)
            }
            get ("content"){
                call.respond(contentService.getHomePage());
            }
        }
    }



}

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()

