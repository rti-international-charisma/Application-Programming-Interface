package com.rti.charisma.api.repository

import com.rti.charisma.api.db.tables.Answer
import com.rti.charisma.api.db.tables.SectionScore
import com.rti.charisma.api.db.tables.SectionScores

interface AssessmentRepository {
    fun insertScore(sections: List<SectionScore>)
    fun userScoreExists(userId: Int): Boolean
    fun replaceScore(sections: List<SectionScore>)
    fun findSectionsByUser(userId: Int): List<SectionScore>
    fun findAnswersByAssessmentSectionId(userSectionId: Int): List<Answer>

}
