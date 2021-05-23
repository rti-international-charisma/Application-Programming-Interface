package com.rti.charisma.api.db.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object SectionScores : Table() {

    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val sectionId = varchar("section_id", 255)
    val sectionType = varchar("section_type", 255)
    val totalSections = integer("total_sections")

    override val primaryKey = PrimaryKey(id, name = "PK_SECTION_SCORE_ID")
}

data class SectionScore(
    var id: Int = 0,
    val user: Int,
    val sectionId: String,
    val sectionType: String,
    val totalSections: Int,
    var answers: List<Answer> = emptyList()
)
