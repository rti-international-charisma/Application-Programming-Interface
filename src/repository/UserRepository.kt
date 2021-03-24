package com.rti.charisma.api.repository

import com.rti.charisma.api.db.tables.User
import com.rti.charisma.api.route.Signup


interface UserRepository {
    fun doesUserExist(username: String): Boolean
    fun registerUser(signup: Signup): Int
    fun findByUserByUsername(username: String): User?
}