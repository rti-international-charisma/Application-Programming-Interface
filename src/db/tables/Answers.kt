package com.rti.charisma.api.db.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Answers : Table() {
    val id = integer("id").autoIncrement()
    val assessmentSectionId = integer("section_id").references(SectionScores.id, onDelete = ReferenceOption.CASCADE)
    val questionId = varchar("question_id", length = 255)
    val score = integer("score")

    override val primaryKey = PrimaryKey(SectionScores.id, name = "PK_ANSWER_ID")
}

data class Answer(var id: Int = 0, var assessmentSectionId: Int = 0, val questionId: String, val score: Int)
