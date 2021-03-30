package com.rti.charisma.api.route

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.rti.charisma.api.service.UserService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

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
}