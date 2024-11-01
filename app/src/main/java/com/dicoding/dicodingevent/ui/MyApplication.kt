package com.dicoding.dicodingevent.ui

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import com.dicoding.dicodingevent.data.local.room.EventDatabase
import com.dicoding.dicodingevent.data.remote.retrofit.ApiConfig
import com.dicoding.dicodingevent.data.repository.EventRepository
import com.dicoding.dicodingevent.ui.settings.SettingPreference
import com.dicoding.dicodingevent.ui.settings.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyApplication : Application() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        val eventDao = EventDatabase.getDatabase(this).eventDao()
        val apiService = ApiConfig.getApiService()
        val eventRepository = EventRepository.getInstance(apiService, eventDao)
        Log.d("MyApplication", "Calling getEvents")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val eventsLiveData = eventRepository.getEvents()
                withContext(Dispatchers.Main) {
                    eventsLiveData.observeForever {
                        Log.d("MyApplication", "getEvents result: $it")
                    }
                }
            } catch (e: Exception) {
                Log.e("MyApplication", "Error calling getEvents", e)
            }
        }
        val pref = SettingPreference.getInstance(dataStore)
        CoroutineScope(Dispatchers.IO).launch {
            pref.getThemeSetting().collect { isDarkModeActive ->
                withContext(Dispatchers.Main) {
                    if (isDarkModeActive) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
            }
        }
    }
}