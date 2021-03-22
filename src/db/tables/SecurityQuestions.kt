package com.rti.charisma.api.db.tables

import org.jetbrains.exposed.sql.Table

object SecurityQuestions: Table() {
    val sec_q_id = integer("sec_q_id").autoIncrement()
    val question = varchar("question", 255)
    override val primaryKey: PrimaryKey = PrimaryKey(sec_q_id)
}

