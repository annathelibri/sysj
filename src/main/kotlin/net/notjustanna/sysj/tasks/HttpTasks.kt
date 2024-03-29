package net.notjustanna.sysj.tasks

import mu.KLogging
import net.notjustanna.sysj.utils.sendAsync
import java.net.URI
import java.net.http.HttpClient
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import java.net.http.HttpResponse.BodyHandlers.ofString as stringHandler

@Singleton
open class HttpTasks {
    companion object : KLogging()

    val schedulerService: ScheduledExecutorService = Executors.newScheduledThreadPool(
        Runtime.getRuntime().availableProcessors()
    )

    private val httpClient: HttpClient = HttpClient.newHttpClient()

    val tasks = ConcurrentHashMap<String, HttpTask>()

    fun createCronTask(url: String, cron: String): String {
        return registerTask(CronTask(this, url, cron))
    }

    fun createRateTask(url: String, rate: Long, unit: TimeUnit): String {
        return registerTask(RateTask(this, url, rate, unit))
    }

    private fun registerTask(task: HttpTask): String {
        val uuid = UUID.randomUUID().toString()
        tasks[uuid] = task
        return uuid
    }

    fun fire(url: String) {
        val res = httpClient.sendAsync(stringHandler()) {
            uri(URI.create(url))
        }

        res.thenAccept {
            val body = it.body()
            if (body.isNotEmpty()) {
                logger.info { "Task '$url' received the following response: \n$body" }
            }
        }
    }

}
