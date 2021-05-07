package com.rti.charisma.api.service

import com.rti.charisma.api.db.tables.Answer
import com.rti.charisma.api.db.tables.SectionScore
import com.rti.charisma.api.repository.AssessmentRepository
import com.rti.charisma.api.route.AssessmentResult
import com.rti.charisma.api.route.Question
import com.rti.charisma.api.route.response.AssessmentScoreResponse

class AssessmentService(private val assessmentRepository: AssessmentRepository) {
    fun addAssessmentScore(userId: Int, assessmentResults: List<AssessmentResult>) {
        if (assessmentRepository.userScoreExists(userId)) {
            assessmentRepository.replaceScore(toSectionScore(userId, assessmentResults))
        } else {
            assessmentRepository.insertScore(toSectionScore(userId, assessmentResults))
        }
    }

    fun getAssessmentScore(userId: Int): AssessmentScoreResponse {
        val sections: List<SectionScore> = assessmentRepository.findSectionsByUser(userId)
        return fromSectionScore(sections)
    }

    private fun fromSectionScore(sections: List<SectionScore>): AssessmentScoreResponse {
        val sectionScores = sections.map { sectionScore ->
            AssessmentResult(
                sectionScore.sectionId,
                sectionScore.sectionType,
                sectionScore.answers.map { answer -> Question(answer.questionId, answer.score) }
            )
        }
        return AssessmentScoreResponse(sectionScores)
    }

    private fun toSectionScore(userId: Int, assessmentResults: List<AssessmentResult>): List<SectionScore> {
        return assessmentResults.map { section ->
            SectionScore(
                user = userId,
                sectionId = section.sectionId,
                sectionType = section.sectionType,
                answers = section.answers
                    .map { question -> Answer(questionId = question.questionId, score = question.score) }
            )
        }
    }



}
