package com.rti.charisma.api.route

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.rti.charisma.api.db.tables.User
import com.rti.charisma.api.model.UsernameAvailability
import com.rti.charisma.api.service.UserService
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post

data class Signup(val username: String, val password: String, val secQuestionId: Int, val secQuestionAnswer: String)
data class Login (val username: String, val password: String)

@KtorExperimentalLocationsAPI
fun Routing.userRoute(userService: UserService) {

    post("/signup") {
        val signupModel = jacksonObjectMapper().readValue<Signup>(call.receiveText())
        userService.registerUser(signupModel)
        call.respond(HttpStatusCode.OK, "User registered ")
    }

    get("/securityquestions/{id}") {
        val id = if (call.parameters["id"].isNullOrEmpty()) {
           null
         } else {
            call.parameters["id"]?.toInt()
        }
        call.respond(HttpStatusCode.OK, userService.getSecurityQuestions(id))
    }

    get("/user/availability/{username}") {
        if (call.parameters["username"].isNullOrEmpty()) {
            call.respond(HttpStatusCode.BadRequest, "Provide username")
        } else {
            val usernameAvailable = userService.isUsernameAvailable(call.parameters["username"]!!)
            call.respond(HttpStatusCode.OK, UsernameAvailability(usernameAvailable))
        }
    }

    post("/login") {
        val loginModel = jacksonObjectMapper().readValue<Login>(call.receiveText())
        call.respond(HttpStatusCode.OK, userService.login(loginModel))
    }

    authenticate("jwt") {
        get("/auth/route") {
            val user = call.principal<User>()
            call.respond(HttpStatusCode.OK, " ${user?.username} is an authenticated user")
        }
    }
}