package net.notjustanna.sysj.model

import net.notjustanna.sysj.tasks.CronTask
import net.notjustanna.sysj.tasks.RateTask
import java.util.concurrent.TimeUnit

sealed class HttpTaskModel {
    abstract val url: String
}

data class CronTaskModel(override val url: String, val cron: String) : HttpTaskModel() {
    constructor(task: CronTask) : this(task.url, task.cron)
}

class RateTaskModel(override val url: String, val rate: Long, val unit: TimeUnit) : HttpTaskModel() {
    constructor(task: RateTask) : this(task.url, task.rate, task.unit)
}
