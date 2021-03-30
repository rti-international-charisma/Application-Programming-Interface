package com.rti.charisma.api.db.tables

import com.fasterxml.jackson.annotation.JsonIgnore
import io.ktor.auth.Principal
import org.jetbrains.exposed.sql.Table

object Users: Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 25)
    val password = varchar("password", 50)
    val sec_q_id = integer("sec_q_id").references(SecurityQuestions.sec_q_id)
    val sec_answer = varchar("sec_answer", 50)
    override val primaryKey: PrimaryKey = PrimaryKey(id)

}

data class User(
    val id: Int,
    val username: String,
    val sec_q_id: Int = 0,

    @JsonIgnore
    val password: String
) : Principal
