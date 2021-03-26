package com.rti.charisma.api.model

import com.rti.charisma.api.db.tables.User

data class UserResponse (
    val user: User,
    val token: String
)