package com.rti.charisma.api.db

import com.rti.charisma.api.config.ConfigProvider
import com.rti.charisma.api.config.DB_PASSWORD
import com.rti.charisma.api.config.DB_URL
import com.rti.charisma.api.config.DB_USER
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

object CharismaDB {

    fun getDataSource(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.postgresql.Driver"
        config.jdbcUrl = ConfigProvider.get(DB_URL)
        config.username = ConfigProvider.get(DB_USER)
        config.password = ConfigProvider.get(DB_PASSWORD)
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }

    fun init () {
        Database.connect(getDataSource())
    }
}