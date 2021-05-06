package com.rti.charisma.api.repository

import com.rti.charisma.api.db.tables.SecurityQuestion
import com.rti.charisma.api.db.tables.SecurityQuestions
import com.rti.charisma.api.db.tables.User
import com.rti.charisma.api.db.tables.Users
import com.rti.charisma.api.route.Signup
import com.rti.charisma.api.util.hash
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

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

    override fun registerUser(signup: Signup, initialLoginAttempts: Int, initialResetPasswordAttempts: Int): Int {
        val user = transaction {
            Users.insert {
                it[username] = signup.username
                it[password] = signup.password.hash()
                it[sec_q_id] = signup.secQuestionId
                it[sec_answer] = signup.secQuestionAnswer.hash()
                it[loginAttempts] = initialLoginAttempts
                it[resetPasswordAttempts] = initialResetPasswordAttempts
            }
        }
        return user[Users.id]
    }

    override fun findUserByUsername(username: String) = transaction {
        Users.select { Users.username eq username }.firstOrNull()?.toUser()
    }

    override fun findUserById(userId: Int): User? = transaction {
        Users.select { Users.id eq userId }.firstOrNull()?.toUser()
    }

    override fun updateUser(user: User) {
        transaction {
            Users.update({ Users.id eq user.id }) {
                it[loginAttempts] = user.loginAttemptsLeft
                it[resetPasswordAttempts] = user.resetPasswordAttemptsLeft
                it[password] = user.password
            }
        }
    }


    private fun ResultRow.toUser(): User = User(
                id =this[Users.id],
                username = this[Users.username],
                sec_q_id = this[Users.sec_q_id],
                password = this[Users.password],
                sec_answer = this[Users.sec_answer],
                loginAttemptsLeft = this[Users.loginAttempts],
                resetPasswordAttemptsLeft = this[Users.resetPasswordAttempts]
        )

    private fun ResultRow.toSecurityQuestion(): SecurityQuestion = SecurityQuestion(
            id = this[SecurityQuestions.sec_q_id],
            question = this[SecurityQuestions.question]
    )
}