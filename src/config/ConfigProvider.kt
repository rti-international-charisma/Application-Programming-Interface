package com.rti.charisma.api.config

import com.typesafe.config.ConfigFactory
import io.ktor.config.*

const val DB_URL = "ktor.db.db_url"
const val DB_USER = "ktor.db.db_user"
const val DB_PASSWORD = "ktor.db.db_password"
const val SECRET_KEY = "ktor.secret"
const val CMS_BASE_URL = "ktor.cms.base_url"
const val ACCESS_TOKEN = "ktor.cms.access_token"

object ConfigProvider {
    private val config = HoconApplicationConfig(ConfigFactory.load())

    fun get(key: String): String = config.property(key).getString()
}

