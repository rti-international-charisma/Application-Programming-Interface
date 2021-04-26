package com.rti.charisma.api.repository

import com.rti.charisma.api.db.tables.Answer
import com.rti.charisma.api.db.tables.Answers
import com.rti.charisma.api.db.tables.SectionScore
import com.rti.charisma.api.db.tables.SectionScores
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class AssessmentRepositoryImpl : AssessmentRepository {
    override fun insertScore(sections: List<SectionScore>) {
        transaction {
            sections.forEach { score ->
                val userSectionId = insertSectionScore(score)
                score.answers.forEach { answer ->
                    insertAnswer(answer, userSectionId)
                }
            }
        }
    }

    override fun userScoreExists(userId: Int): Boolean = transaction {
        SectionScores.select { SectionScores.userId eq userId }.firstOrNull()?.let { true } ?: false
    }

    override fun replaceScore(sections: List<SectionScore>) {
        transaction {
            if (sections.isNotEmpty()){
                SectionScores.deleteWhere { SectionScores.userId eq sections[0].user }
                sections.forEach { score ->
                    val userSectionId = insertSectionScore(score)
                    score.answers.forEach { answer ->
                        insertAnswer(answer, userSectionId)
                    }
                }
            }
        }
    }

    override fun findByUser(userId: Int): List<SectionScores> {
        TODO("Not yet implemented")
    }


    private fun insertAnswer(answer: Answer, assessmentSectionId: Int) {
        Answers.insert {
            it[questionId] = answer.questionId
            it[score] = answer.score
            it[Answers.assessmentSectionId] = assessmentSectionId
        }
    }

    private fun insertSectionScore(section: SectionScore): Int {
        return SectionScores.insert {
            it[userId] = section.user
            it[sectionId] = section.sectionId
            it[sectionType] = section.sectionType
        } get SectionScores.id
    }

}
