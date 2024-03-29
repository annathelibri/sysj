package net.notjustanna.sysj.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import mu.KLogging
import net.notjustanna.sysj.model.CronTaskModel
import net.notjustanna.sysj.model.HttpTaskModel
import net.notjustanna.sysj.model.RateTaskModel
import net.notjustanna.sysj.tasks.CronTask
import net.notjustanna.sysj.tasks.HttpTasks
import net.notjustanna.sysj.tasks.RateTask

@Controller("/httpTasks")
class HttpTasksController(private val httpTasks: HttpTasks) {
    companion object : KLogging()

    @Get
    fun index(): HttpResponse<Map<String, HttpTaskModel>> {
        val map = httpTasks.tasks.mapValues { (_, v) ->
            when (v) {
                is CronTask -> CronTaskModel(v)
                is RateTask -> RateTaskModel(v)
            }
        }

        return HttpResponse.created(map)
    }

    @Post("cron")
    fun createCron(@Body model: CronTaskModel): HttpResponse<Map<String, String>> {
        val uuid = httpTasks.createCronTask(model.url, model.cron)
        return HttpResponse.created(mapOf("uuid" to uuid))
    }

    @Post("rate")
    fun createRate(@Body model: RateTaskModel): HttpResponse<Map<String, String>> {
        val uuid = httpTasks.createRateTask(model.url, model.rate, model.unit)
        return HttpResponse.created(mapOf("uuid" to uuid))
    }

    @Get("stop/{uuid}")
    fun stop(@PathVariable uuid: String): HttpResponse<Boolean> {
        val task = httpTasks.tasks[uuid] ?: return HttpResponse.notFound(false)
        task.stop()
        return HttpResponse.ok(true)
    }
}
