package com.rti.charisma.api.route.response

import com.rti.charisma.api.db.tables.User

data class UserResponse (
    val user: User,
    val token: String
)