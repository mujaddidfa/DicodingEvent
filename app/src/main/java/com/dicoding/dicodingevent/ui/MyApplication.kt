package com.dicoding.dicodingevent.ui

import android.app.Application
import com.dicoding.dicodingevent.data.local.room.EventDatabase
import com.dicoding.dicodingevent.data.remote.retrofit.ApiConfig
import com.dicoding.dicodingevent.data.repository.EventRepository
import com.dicoding.dicodingevent.utils.AppExecutors

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val eventDao = EventDatabase.getDatabase(this).eventDao()
        val apiService = ApiConfig.getApiService()
        val appExecutors = AppExecutors()
        val eventRepository = EventRepository.getInstance(apiService, eventDao, appExecutors)
        eventRepository.getEvents()
    }
}