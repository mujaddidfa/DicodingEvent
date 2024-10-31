package com.dicoding.dicodingevent.data.remote.retrofit

import com.dicoding.dicodingevent.data.remote.response.EventResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("events")
    fun getEvents(@Query("active") active: Int): EventResponse

    @GET("events")
    suspend fun getEventLimited(@Query("active") active: Int, @Query("limit") limit: Int): Response<EventResponse>
}