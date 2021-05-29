package com.rti.charisma.api

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.rti.charisma.api.client.ContentClient
import com.rti.charisma.api.config.*
import com.rti.charisma.api.db.CharismaDB
import com.rti.charisma.api.exception.*
import com.rti.charisma.api.repository.AssessmentRepositoryImpl
import com.rti.charisma.api.repository.UserRepositoryImpl
import com.rti.charisma.api.route.assessmentRoute
import com.rti.charisma.api.route.contentRoute
import com.rti.charisma.api.route.healthRoute
import com.rti.charisma.api.route.response.ErrorResponse
import com.rti.charisma.api.route.userRoute
import com.rti.charisma.api.service.*
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
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.util.concurrent.TimeUnit
import javax.sql.DataSource

fun main() {
    embeddedServer(Netty, port = 8080) {
        main()
    }.start(wait = true)
}

@kotlin.jvm.JvmOverloads
fun Application.main() {
    val logger = LoggerFactory.getLogger("Application_Charisma")

    val contentClient = ContentClient()
    val contentService = ContentService(contentClient)
    val userService = UserService(UserRepositoryImpl(), JWTService)
    val assessmentService = AssessmentService(AssessmentRepositoryImpl())
    val cleanupTask = CleanupTask(userService)
    val schedulerService = SchedulerService(cleanupTask)

    commonModule()
    loginModule(getDataSource(), userService, assessmentService)
    contentModule(contentService)
    healthCheckModule()

    environment.monitor.subscribe(ApplicationStarted) {
        logger.info("Invoking scheduler...")
        schedulerService.scheduleExecution(Every(ConfigProvider.get(SCHEDULER_FREQUENCY).toLong(), TimeUnit.DAYS))
    }

    environment.monitor.subscribe(ApplicationStopping) {
        logger.info("Stopping all processes...")
        schedulerService.stop()
        contentClient.close()
    }
}

fun Application.commonModule() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(CachingHeaders) {
        options { outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Application.Json -> CachingOptions(
                    CacheControl.MaxAge(maxAgeSeconds = ConfigProvider.get(CACHE_MAX_AGE_SECONDS).toInt()),
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
            registerModule(JavaTimeModule()) // support java.time.* types
        }
    }

    install(StatusPages) {
        exception<ContentRequestException> { e ->
            call.respond(HttpStatusCode.BadGateway, ErrorResponse(e.localizedMessage))
        }
        exception<ContentException> { e ->
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse(e.localizedMessage))
        }
        exception<ContentServerException> { e ->
            call.respond(HttpStatusCode.BadGateway, ErrorResponse(e.localizedMessage))
        }
        exception<DataBaseException> { e ->
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

        exception<ResetPasswordAttemptsExhaustedException> { e ->
            call.respond(HttpStatusCode.Unauthorized, ErrorResponse(e.localizedMessage))
        }

        exception<Throwable> {
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    install(CORS) {
        anyHost()
        header("Content-Type")
        header("Authorization")
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
    config.maximumPoolSize = 10
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