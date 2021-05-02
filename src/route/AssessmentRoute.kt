package com.rti.charisma.api.route

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.rti.charisma.api.db.tables.User
import com.rti.charisma.api.service.AssessmentService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

data class AssessmentResult(val sectionId: String, val sectionType: String, val answers: List<Question>)
data class Question(val questionId: String, val score: Int)

@KtorExperimentalLocationsAPI
fun Routing.assessmentRoute(assessmentService: AssessmentService) {


    authenticate("jwt") {
        post("assessment/scores") {
            val user = call.principal<User>()
            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized)
            } else {
                val assessmentScore = jacksonObjectMapper().readValue<List<AssessmentResult>>(call.receiveText())
                assessmentService.addAssessmentScore(user.id, assessmentScore)
                call.respond(HttpStatusCode.Created)
            }
        }

        get("assessment/scores") {
            val user = call.principal<User>()
            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized)
            } else {
                val assessmentScores = assessmentService.getAssessmentScore(user.id)
                call.respond(assessmentScores)
            }
        }
    }
}
