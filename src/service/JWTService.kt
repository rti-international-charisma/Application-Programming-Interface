package com.rti.charisma.api.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.rti.charisma.api.config.ConfigProvider
import com.rti.charisma.api.config.JWT_SECRET
import com.rti.charisma.api.db.tables.User
import java.util.*

object JWTService {

    private const val issuer = "charismaApi"
    private val algorithm = Algorithm.HMAC512(ConfigProvider.get(JWT_SECRET))
    private const val validityInMs = 60000 * 15 // 15 minutes (milli seconds in minute * X)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    fun generateToken(user: User): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim("id", user.id)
        .withExpiresAt(expiresAt())
        .sign(algorithm)


    private fun expiresAt() = Date(System.currentTimeMillis() + validityInMs)
}
