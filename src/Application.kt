package com.rti.charisma.api

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.rti.charisma.api.client.ContentClient
import com.rti.charisma.api.config.ConfigProvider
import com.rti.charisma.api.config.DB_PASSWORD
import com.rti.charisma.api.config.DB_URL
import com.rti.charisma.api.config.DB_USER
import com.rti.charisma.api.db.CharismaDB
import com.rti.charisma.api.exception.ContentNotFoundException
import com.rti.charisma.api.exception.SecurityQuestionException
import com.rti.charisma.api.exception.UserAlreadyExistException
import com.rti.charisma.api.repository.UserRepositoryImpl
import com.rti.charisma.api.route.contentRoute
import com.rti.charisma.api.route.userRoute
import com.rti.charisma.api.service.UserService
import com.viartemev.ktor.flyway.FlywayFeature
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
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
import javax.sql.DataSource


fun main() {
    embeddedServer(Netty, port = 5000) {
        main()
    }.start(wait = true)
}

@kotlin.jvm.JvmOverloads
fun Application.main() {
    val contentService = ContentService(ContentClient())
    val userService = UserService(UserRepositoryImpl())

    commonModule()
    loginModule(getDataSource(), userService)
    contentModule(contentService)
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
                    CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60),
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
        exception<Throwable> {
            call.respond(HttpStatusCode.InternalServerError)
        }

        exception<ContentNotFoundException> {
            call.respond(HttpStatusCode.InternalServerError, "Error fetching data")
        }

        exception<UserAlreadyExistException> {
            call.respond(HttpStatusCode.BadRequest, "Username already exists")
        }

        exception<SecurityQuestionException> { e ->
            call.respond(HttpStatusCode.BadRequest, e.localizedMessage)
        }
    }

    install(CORS) {
        anyHost()
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

fun Application.loginModule(postgresDbDataSource: DataSource, userService: UserService) {

    CharismaDB.init(postgresDbDataSource)

    install(FlywayFeature) {
        dataSource = postgresDbDataSource
    }

    routing {
        userRoute(userService)
    }
}

fun Application.contentModule(contentService: ContentService) {

    routing {
        contentRoute(contentService)
    }

}



