package com.rti.charisma.api.config

import com.typesafe.config.ConfigFactory
import io.ktor.config.HoconApplicationConfig

const val DB_URL = "ktor.db.db_url"
const val DB_USER = "ktor.db.db_user"
const val DB_PASSWORD = "ktor.db.db_password"

object ConfigProvider {
    private val config = HoconApplicationConfig(ConfigFactory.load())

    fun get(key: String): String = config.property(key).getString()
}
