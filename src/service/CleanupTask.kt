package com.rti.charisma.api.service

import com.rti.charisma.api.config.ConfigProvider
import com.rti.charisma.api.config.INACTIVE_THRESHOLD_IN_DAYS
import org.slf4j.LoggerFactory

/**
 * Runnable to purge inactive users. Users which are inactive for [INACTIVE_THRESHOLD_IN_DAYS] Days
 * Determined by the user's last_login datetime.
 */
class CleanupTask(private val userService: UserService) : Runnable {
    private val logger = LoggerFactory.getLogger(CleanupTask::class.java)
    private val durationInDays = ConfigProvider.get(INACTIVE_THRESHOLD_IN_DAYS)

    override fun run() {
        logger.info("Delete inactive users started")
        val deletedRecords = userService.deleteInactiveUsers(durationInDays.toLong())
        logger.info("Delete inactive users complete, deleted records: $deletedRecords")
    }
}
