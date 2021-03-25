package com.rti.charisma.api.repository

import com.rti.charisma.api.config.ConfigProvider
import com.rti.charisma.api.config.SECRET_KEY
import com.rti.charisma.api.db.tables.SecurityQuestion
import com.rti.charisma.api.db.tables.SecurityQuestions
import com.rti.charisma.api.db.tables.User
import com.rti.charisma.api.db.tables.Users
import com.rti.charisma.api.route.Signup
import io.ktor.util.hex
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class UserRepositoryImpl: UserRepository {

    override fun getSecurityQuestions(secQId: Int?): List<SecurityQuestion> {
        secQId?.let {
            return transaction {
                SecurityQuestions.select { SecurityQuestions.sec_q_id eq it }.map { it.toSecurityQuestion() }
            }
        } ?: run {
            return transaction {
                SecurityQuestions.selectAll().map { it.toSecurityQuestion() }
            }
        }
    }

    override fun doesUserExist(username: String): Boolean = transaction {
        Users.select { Users.username eq username }.firstOrNull()?.let { true } ?: false
    }

    override fun registerUser(signup: Signup): Int {
        val user = transaction {
            Users.insert {
                it[username] = signup.username
                it[password] = signup.password.hash()
                it[sec_q_id] = signup.secQuestionId
                it[sec_answer] = signup.secQuestionAnswer.hash()
            }
        }
        return user[Users.id]
    }

    override fun findByUserByUsername(username: String) = transaction {
            Users.select { Users.username eq id }.firstOrNull()?.toUser()
        }


    private fun ResultRow.toUser(): User = User(
                id =this[Users.id],
                username = this[Users.username],
                password = this[Users.password]
        )

    private fun ResultRow.toSecurityQuestion(): SecurityQuestion = SecurityQuestion(
            id = this[SecurityQuestions.sec_q_id],
            question = this[SecurityQuestions.question]
    )

    private fun String.hash(): String {
        val hashKey = ConfigProvider.get(SECRET_KEY).toByteArray()

        val hmacKey = SecretKeySpec(hashKey, "HmacSHA1")

        val hmac = Mac.getInstance("HmacSHA1")
        hmac.init(hmacKey)
        return hex(hmac.doFinal(this.toByteArray(Charsets.UTF_8)))
    }
}