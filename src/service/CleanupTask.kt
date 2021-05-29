package com.rti.charisma.api.service

import com.rti.charisma.api.config.ConfigProvider
import com.rti.charisma.api.config.INACTIVE_THRESHOLD_IN_DAYS
import org.slf4j.LoggerFactory

class CleanupTask(private val userService: UserService) : Runnable {
    private val logger = LoggerFactory.getLogger(CleanupTask::class.java)
    private val durationInDays = ConfigProvider.get(INACTIVE_THRESHOLD_IN_DAYS)

    override fun run() {
        logger.info("Delete inactive users started")
        val deletedRecords = userService.deleteInactiveUsers(durationInDays.toLong())
        logger.info("Delete inactive users complete, deleted records: $deletedRecords")
    }
}
