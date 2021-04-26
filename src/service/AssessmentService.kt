package com.rti.charisma.api.service

import com.rti.charisma.api.db.tables.Answer
import com.rti.charisma.api.db.tables.SectionScore
import com.rti.charisma.api.repository.AssessmentRepository
import com.rti.charisma.api.route.AssessmentResult
import com.rti.charisma.api.route.Question
import java.util.stream.Collectors

class AssessmentService(private val assessmentRepository: AssessmentRepository) {


    fun addAssessmentScore(userId: Int, assessmentResults: List<AssessmentResult>) {
        if (assessmentRepository.userScoreExists(userId)) {
            assessmentRepository.replaceScore(toSectionScore(userId, assessmentResults))
        } else {
            assessmentRepository.insertScore(toSectionScore(userId, assessmentResults))
        }
    }

    private fun toSectionScore(userId: Int, assessmentResults: List<AssessmentResult>): List<SectionScore> {
        return assessmentResults.stream()
            .map { section ->
            SectionScore(
                user = userId,
                sectionId = section.sectionId,
                sectionType = section.sectionType,
                answers = toData(section.questions)
            )
        }.collect(Collectors.toList())
    }

    private fun toData(questions: List<Question>): List<Answer> {
       return questions.stream().map{ question ->
            Answer(questionId = question.questionId, score = question.score)
        }.collect(Collectors.toList())
    }
}
