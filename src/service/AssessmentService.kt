package com.rti.charisma.api.service

import com.rti.charisma.api.db.tables.Answer
import com.rti.charisma.api.db.tables.SectionScore
import com.rti.charisma.api.db.tables.SectionScores
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

    fun getAssessmentScore(userId: Int) : AssessmentScoreResponse {
        val scores = assessmentRepository.findByUser(userId)
        return fromSectionScore(scores);
    }

    private fun fromSectionScore(scores: List<SectionScores>): AssessmentScoreResponse {
        TODO("Not yet implemented")
    }


    private fun toSectionScore(userId: Int, assessmentResults: List<AssessmentResult>): List<SectionScore> {
        return assessmentResults.map { section ->
            SectionScore(
                user = userId,
                sectionId = section.sectionId,
                sectionType = section.sectionType,
                answers = toData(section.answers)
            )
        }
    }

    private fun toData(questions: List<Question>): List<Answer> {
       return questions.map{ question ->
            Answer(questionId = question.questionId, score = question.score)
        }
    }


}
