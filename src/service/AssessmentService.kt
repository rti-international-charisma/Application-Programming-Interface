package com.rti.charisma.api.service

import com.rti.charisma.api.db.tables.Answer
import com.rti.charisma.api.db.tables.SectionScore
import com.rti.charisma.api.exception.DataBaseException
import com.rti.charisma.api.repository.AssessmentRepository
import com.rti.charisma.api.route.AssessmentResult
import com.rti.charisma.api.route.Question
import com.rti.charisma.api.route.response.AssessmentScoreResponse
import org.slf4j.LoggerFactory

class AssessmentService(private val assessmentRepository: AssessmentRepository) {
    private val logger = LoggerFactory.getLogger(AssessmentService::class.java)

    fun addAssessmentScore(userId: Int, assessmentResults: List<AssessmentResult>, totalSections: Int) {
        try {
            if (assessmentRepository.userScoreExists(userId)) {
                logger.info("Updating assessment scores for, $userId")
                assessmentRepository.replaceScore(toSectionScore(userId, assessmentResults, totalSections))
            } else {
                logger.info("Inserting assessment scores for, $userId")
                assessmentRepository.insertScore(toSectionScore(userId, assessmentResults, totalSections))
            }
        } catch (e: Exception) {
            logger.error("Failed to update scores for, $userId, ${e.printStackTrace()}")
            throw DataBaseException("Failed to update scores", e)
        }
    }

    fun getAssessmentScore(userId: Int): AssessmentScoreResponse {
        try {
            val sections: List<SectionScore> = assessmentRepository.findSectionsByUser(userId)
            logger.info("Successfully fetched assessment scores for, $userId")
            return fromSectionScore(sections)
        } catch (e: Exception) {
            logger.error("Failed to get scores for, $userId, ${e.printStackTrace()}")
            throw DataBaseException("Failed to get scores", e)
        }
    }

    private fun fromSectionScore(sections: List<SectionScore>): AssessmentScoreResponse {
        val sectionScores = sections.map { sectionScore ->
            AssessmentResult(
                sectionScore.sectionId,
                sectionScore.sectionType,
                sectionScore.answers.map { answer -> Question(answer.questionId, answer.score) }
            )
        }
        return AssessmentScoreResponse(sectionScores, sections.firstOrNull()?.let { it -> it.totalSections } ?: 0)
    }

    private fun toSectionScore(userId: Int, assessmentResults: List<AssessmentResult>, totalSections: Int): List<SectionScore> {
        return assessmentResults.map { section ->
            SectionScore(
                user = userId,
                sectionId = section.sectionId,
                sectionType = section.sectionType,
                totalSections = totalSections,
                answers = section.answers
                    .map { question -> Answer(questionId = question.questionId, score = question.score) }
            )
        }
    }

}
