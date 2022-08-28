package dev.timatifey.posanie.api

import dev.timatifey.posanie.model.cache.Scheduler
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SchedulerAPI {

    @GET("/api/v1/ruz/scheduler/{group}")
    suspend fun getScheduler(
        @Path("group") groupId: Long,
        @Query("date") date: String
    ): Scheduler

}
