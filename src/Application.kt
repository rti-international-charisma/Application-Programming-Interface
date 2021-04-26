package com.rti.charisma.api

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.rti.charisma.api.client.ContentClient
import com.rti.charisma.api.config.ConfigProvider
import com.rti.charisma.api.config.DB_PASSWORD
import com.rti.charisma.api.config.DB_URL
import com.rti.charisma.api.config.DB_USER
import com.rti.charisma.api.db.CharismaDB
import com.rti.charisma.api.exception.*
import com.rti.charisma.api.repository.AssessmentRepositoryImpl
import com.rti.charisma.api.repository.UserRepositoryImpl
import com.rti.charisma.api.route.assessmentRoute
import com.rti.charisma.api.route.contentRoute
import com.rti.charisma.api.route.healthRoute
import com.rti.charisma.api.route.response.ErrorResponse
import com.rti.charisma.api.route.userRoute
import com.rti.charisma.api.service.AssessmentService
import com.rti.charisma.api.service.JWTService
import com.rti.charisma.api.service.UserService
import com.viartemev.ktor.flyway.FlywayFeature
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
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
import javax.sql.DataSource


fun main() {
    embeddedServer(Netty, port = 8080) {
        main()
    }.start(wait = true)
}

@kotlin.jvm.JvmOverloads
fun Application.main() {
    val contentService = ContentService(ContentClient())
    val userService = UserService(UserRepositoryImpl(), JWTService)
    val assessmentService = AssessmentService(AssessmentRepositoryImpl())

    commonModule()
    loginModule(getDataSource(), userService, assessmentService)
    contentModule(contentService)
    healthCheckModule()
}

fun Application.commonModule() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(CachingHeaders) {
        options { outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.CSS -> CachingOptions(
                    CacheControl.MaxAge(maxAgeSeconds = 60),
                    expires = null as? GMTDate?
                )
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
        exception<ContentRequestException> { e ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.localizedMessage))
        }

        exception<ContentException> { e ->
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse(e.localizedMessage))
        }

        exception<UserAlreadyExistException> {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Username already exists"))
        }

        exception<SecurityQuestionException> { e ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.localizedMessage))
        }

        exception<LoginException> { e ->
            call.respond(HttpStatusCode.Unauthorized, ErrorResponse(e.localizedMessage))
        }

        exception<LoginAttemptsExhaustedException> { _ ->
            call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Reset Password"))
        }

        exception<Throwable> {
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    install(CORS) {
        anyHost()
        header("Content-Type")
        allowCredentials = true
        header(HttpHeaders.AccessControlAllowOrigin)
    }
}

fun getDataSource(): HikariDataSource {
    val config = HikariConfig()
    config.driverClassName = "org.postgresql.Driver"
    config.jdbcUrl = ConfigProvider.get(DB_URL)
    config.username = ConfigProvider.get(DB_USER)
    config.password = ConfigProvider.get(DB_PASSWORD)
    config.maximumPoolSize = 3
    config.isAutoCommit = false
    config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    config.validate()
    return HikariDataSource(config)
}

fun Application.loginModule(
    postgresDbDataSource: DataSource,
    userService: UserService,
    assessmentService: AssessmentService
) {

    CharismaDB.init(postgresDbDataSource)

    install(Authentication) {
        jwt("jwt") {
            verifier(JWTService.verifier)
            realm = "CharismaApi"
            validate {
                val payload = it.payload
                val claim = payload.getClaim("id")
                val claimString = claim.asInt()
                val user = userService.findUserById(claimString)
                user
            }
        }
    }

    install(FlywayFeature) {
        dataSource = postgresDbDataSource
    }

    routing {
        userRoute(userService)
        assessmentRoute(assessmentService)
    }
}

fun Application.contentModule(contentService: ContentService) {

    routing {
        contentRoute(contentService)
    }

}

fun Application.healthCheckModule() {
    routing {
        healthRoute()
    }
}


