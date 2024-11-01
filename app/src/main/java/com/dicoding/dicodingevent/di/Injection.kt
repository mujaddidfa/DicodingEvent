package com.dicoding.dicodingevent.di

import android.content.Context
import com.dicoding.dicodingevent.data.local.room.EventDatabase
import com.dicoding.dicodingevent.data.repository.EventRepository
import com.dicoding.dicodingevent.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getDatabase(context)
        val dao = database.eventDao()
        return EventRepository.getInstance(apiService, dao)
    }
}