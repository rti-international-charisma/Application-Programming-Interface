package com.rti.charisma.api.config

import com.typesafe.config.ConfigFactory
import io.ktor.config.*

const val DB_URL = "ktor.db.db_url"
const val DB_USER = "ktor.db.db_user"
const val DB_PASSWORD = "ktor.db.db_password"
const val HASH_SECRET_KEY = "ktor.hash_secret"
const val JWT_SECRET = "ktor.jwt_secret"
const val CMS_BASE_URL = "ktor.cms.base_url"
const val ACCESS_TOKEN = "ktor.cms.access_token"

/**
 * LoginAttempts are configurable. As of 6th July 2021 it is set to 5.
 * LoginAttempts reset when user logs in with correct password
 */
const val LOGIN_ATTEMPTS = "ktor.login_attempts"

/**
 * Reset Password Attempts are configurable. The value is set through
 * Reset Password Attempts reset when resets password with correct
 */
const val RESET_ATTEMPTS = "ktor.reset_password_attempts"
const val IS_DRAFT_MODE = "ktor.cms.draft_mode"

/**
 * Inactive accounts are purged after these days.
 */
const val INACTIVE_THRESHOLD_IN_DAYS = "ktor.inactive_days_threshold"
const val CACHE_MAX_AGE_SECONDS = "ktor.cache_max_age_seconds"
const val SCHEDULER_FREQUENCY = "ktor.scheduler_frequency"

object ConfigProvider {
    private val config = HoconApplicationConfig(ConfigFactory.load())

    fun get(key: String): String = config.property(key).getString()
}
