package com.rti.charisma.api.repository

import com.rti.charisma.api.db.tables.SecurityQuestion
import com.rti.charisma.api.db.tables.User
import com.rti.charisma.api.route.Signup

interface UserRepository {

    fun getSecurityQuestions(secQId: Int?): List<SecurityQuestion>
    fun doesUserExist(username: String): Boolean
    fun registerUser(signup: Signup, initialLoginAttempts: Int, initialResetPasswordAttempts: Int): Int
    fun findUserByUsername(username: String): User?
    fun findUserById(userId: Int): User?
    fun updateUser(user: User)
    fun deleteInactiveUsers(inactivityInDays: Long) : Int
}