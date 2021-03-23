package com.rti.charisma.api.repository

import com.rti.charisma.api.Signup

interface UserRepository {
    fun doesUserExist(username: String): Boolean
    fun registerUser(signup: Signup): Int
}