package com.rti.charisma.api.db

import org.jetbrains.exposed.sql.Database
import javax.sql.DataSource

object CharismaDB {

    fun init (dataSource: DataSource) = Database.connect(dataSource)

}