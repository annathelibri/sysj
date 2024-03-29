package net.notjustanna.sysj.tasks

import io.micronaut.scheduling.cron.CronExpression
import net.notjustanna.sysj.utils.nextTime
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

sealed class HttpTask {
    protected abstract val parent: HttpTasks
    abstract val url: String

    protected open fun run() {
        parent.fire(url)
    }

    abstract fun stop()
}

data class CronTask(
    override val parent: HttpTasks,
    override val url: String,
    val cron: String
) : HttpTask() {
    private val cronExpr = CronExpression.create(cron)

    private var lastFuture = scheduleNext()

    override fun run() {
        super.run()
        lastFuture = scheduleNext()
    }

    private fun scheduleNext(): ScheduledFuture<*> {
        val duration = cronExpr.nextTime()
        return parent.schedulerService.schedule(this::run, duration.toMillis(), TimeUnit.MILLISECONDS)
    }

    override fun stop() {
        lastFuture.cancel(false)
    }
}

class RateTask(
    override val parent: HttpTasks,
    override val url: String,
    val rate: Long,
    val unit: TimeUnit
) : HttpTask() {
    private val future: ScheduledFuture<*> = parent.schedulerService.scheduleAtFixedRate(this::run, 0, rate, unit)

    override fun stop() {
        future.cancel(false)
    }
}
