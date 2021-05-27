package com.rti.charisma.api.service

import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class SchedulerService(private val task: Runnable) {
    private val logger = LoggerFactory.getLogger(SchedulerService::class.java)

    private val executor = Executors.newScheduledThreadPool(1)!!

    fun scheduleExecution(every: Every) {
        val taskWrapper = Runnable {
            task.run()
        }
        executor.scheduleWithFixedDelay(taskWrapper, every.numberOf, every.numberOf, every.unit)
    }

    fun stop() {
        executor.shutdown()
        try {
            executor.awaitTermination(1, TimeUnit.HOURS)
        } catch (e: InterruptedException) {
            logger.warn("failed to shutdown scheduler")
        }
    }
}

data class Every(val numberOf: Long, val unit: TimeUnit)