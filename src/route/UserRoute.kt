package com.rti.charisma.api.route

import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.impl.JWTParser
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.rti.charisma.api.db.tables.User
import com.rti.charisma.api.route.response.Response
import com.rti.charisma.api.route.response.UsernameAvailability
import com.rti.charisma.api.service.JWTService
import com.rti.charisma.api.service.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.JWTCredential
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.util.*

data class Signup(val username: String, val password: String, val secQuestionId: Int, val secQuestionAnswer: String)
data class Login(val username: String, val password: String)
data class VerifySecQuestion(val username: String, val secQuestionId: Int, val secQuestionAnswer: String)
data class ResetPassword(val newPassword: String)

/**
 * post("/signup") :
 * Register User
 *
 * get("/securityquestions/{id}") :
 * Returns List of securityQuestions. If id is passed then returns List with single security question.
 *
 * get("/user/availability/{username}") :
 * Return [UsernameAvailability] with true or false
 *
 * post("/login") :
 * Login user with credentials. Returns [UserResponse] with JWT valid for [JWTService.validityInMs]
 *
 * post("/verify-securityquestion") :
 * Used for authentication user to reset password.
 * Verifies Security question - Users answer combination. If correct returns JWT for reset password with [JWTService.validityInMs] validity.
 *
 * post("/reset-password") :
 * Authenticated through Reset password JWT generated in post("/verify-securityquestion")
 * If JWT is valid then updates password.
 *
 * @see [JWTService]
 *
 */
@KtorExperimentalLocationsAPI
fun Routing.userRoute(userService: UserService) {

    post("/signup") {
        val signupModel = jacksonObjectMapper().readValue<Signup>(call.receiveText())
        userService.registerUser(signupModel)
        call.respond(HttpStatusCode.OK, Response("User registered "))
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
            call.respond(HttpStatusCode.BadRequest, Response("Provide username"))
        } else {
            val usernameAvailable = userService.isUsernameAvailable(call.parameters["username"]!!)
            call.respond(HttpStatusCode.OK, UsernameAvailability(usernameAvailable))
        }
    }

    post("/login") {
        val loginModel = jacksonObjectMapper().readValue<Login>(call.receiveText())
        call.respond(HttpStatusCode.OK, userService.login(loginModel))
    }

    post("/verify-securityquestion") {
        val verifySecQuestion = jacksonObjectMapper().readValue<VerifySecQuestion>(call.receiveText())
        call.respond(HttpStatusCode.OK, userService.verifySecurityQuestion(verifySecQuestion))
    }

    /**
     * Used specifically for Reset Password JWT verification.
     * Verification done manually as Ktor does not allow more than one [Authentication] plugins.
     */
    fun verifyToken(token: String?): Principal? {
        val decodedJWT = try {
            token?.substringAfter(" ")?.let { JWTService.resetPassVerifier.verify(it) }
        } catch (ex: JWTVerificationException) {
            null
        }

        decodedJWT?.let {
            val parsePayload = JWTParser().parsePayload(String(Base64.getUrlDecoder().decode(decodedJWT.payload)))
            val credentials = JWTCredential(parsePayload)
            val payload = credentials.payload
            val claim = payload.getClaim("id")
            val claimString = claim.asInt()

            return userService.findUserById(claimString)
        }
        return null
    }

    post("/reset-password") {
        val token = call.request.header("Authorization")

        val principal = verifyToken(token)
        principal?.let {
            principal as User
            val resetPassword = jacksonObjectMapper().readValue<ResetPassword>(call.receiveText())
            userService.updatePassword(principal.id, resetPassword.newPassword)
            call.respond(HttpStatusCode.OK)
        } ?: run {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }

    authenticate("jwt") {
        get("/auth/route") {
            val user = call.principal<User>()
            call.respond(HttpStatusCode.OK, Response("${user?.username} is an authenticated user"))
        }
    }
}