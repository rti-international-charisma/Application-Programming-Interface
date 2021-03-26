package com.rti.charisma.api.config

import com.typesafe.config.ConfigFactory
import io.ktor.config.HoconApplicationConfig

const val DB_URL = "ktor.db.db_url"
const val DB_USER = "ktor.db.db_user"
const val DB_PASSWORD = "ktor.db.db_password"
const val HASH_SECRET_KEY = "ktor.hash_secret"
const val JWT_SECRET = "ktor.jwt_secret"


object ConfigProvider {
    private val config = HoconApplicationConfig(ConfigFactory.load())

    fun get(key: String): String = config.property(key).getString()
}
