package com.rti.charisma.api.repository

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

object InMemoryDB {
    fun inMemoryDataSource(): DataSource {
        val config = HikariConfig()
        config.jdbcUrl = "jdbc:h2:mem:testDb;DB_CLOSE_DELAY=-1"
        config.driverClassName = "org.h2.Driver"
        config.maximumPoolSize = 2
        config.validate()
        return HikariDataSource(config)
    }


}