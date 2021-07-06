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

data class AssessmentScoreRequest(val sections: List<AssessmentResult>, val totalSections: Int)
data class AssessmentResult(val sectionId: String, val sectionType: String, val answers: List<Question>)
data class Question(val questionId: String, val score: Int)

/**
 *  post("assessment/scores") :
 *  This is an authenticated API. Stores the AssessmentScore against the user.
 *
 *  get("assessment/scores") :
 *  This is an authenticate API. Returns the Assessment Score stored against the user.
 *
 */
@KtorExperimentalLocationsAPI
fun Routing.assessmentRoute(assessmentService: AssessmentService) {
    authenticate("jwt") {
        post("assessment/scores") {
            val user = call.principal<User>()
            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized)
            } else {
                val assessmentScore = jacksonObjectMapper().readValue<AssessmentScoreRequest>(call.receiveText())
                assessmentService.addAssessmentScore(user.id, assessmentScore.sections, assessmentScore.totalSections)
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
