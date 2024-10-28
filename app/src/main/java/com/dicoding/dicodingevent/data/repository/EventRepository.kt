package com.dicoding.dicodingevent.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.dicoding.dicodingevent.data.local.entity.EventEntity
import com.dicoding.dicodingevent.data.local.room.EventDao
import com.dicoding.dicodingevent.data.remote.retrofit.ApiService
import com.dicoding.dicodingevent.utils.AppExecutors
import com.dicoding.dicodingevent.data.Result
import com.dicoding.dicodingevent.data.remote.response.EventResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EventRepository private constructor(
    private val apiService: ApiService,
    private val eventDao: EventDao,
    private val appExecutors: AppExecutors
) {
    private val result = MediatorLiveData<Result<List<EventEntity>>>()

    fun getEvents(): LiveData<Result<List<EventEntity>>> {
        result.value = Result.Loading
        val client = apiService.getEvents(-1)
        client.enqueue(object : Callback<EventResponse> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    val events = response.body()?.listEvents
                    val eventList = ArrayList<EventEntity>()
                    appExecutors.diskIO.execute {
                        events?.forEach { event ->
                            val isActive = checkEventActive(event.beginTime)
                            val isFavorite = eventDao.isFavorite(event.id)
                            val eventEntity = EventEntity(
                                event.id,
                                event.name,
                                event.summary,
                                event.mediaCover,
                                event.registrants,
                                event.imageLogo,
                                event.link,
                                event.description,
                                event.ownerName,
                                event.cityName,
                                event.quota,
                                event.beginTime,
                                event.endTime,
                                event.category,
                                isActive,
                                isFavorite
                            )
                            eventList.add(eventEntity)
                        }
                        eventDao.deleteAll()
                        eventDao.insertEvents(eventList)
                    }
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }
        })
        val localData = eventDao.getAllEvents()
        result.addSource(localData) { newData: List<EventEntity> ->
            result.value = Result.Success(newData)
        }
        return result
    }

    fun getUpcomingEvents(): LiveData<Result<List<EventEntity>>> {
        val result = MediatorLiveData<Result<List<EventEntity>>>()
        result.value = Result.Loading
        val localData = eventDao.getUpcomingEvents()
        result.addSource(localData) { newData: List<EventEntity> ->
            result.value = Result.Success(newData)
        }
        return result
    }

    fun getFinishedEvents(): LiveData<Result<List<EventEntity>>> {
        val result = MediatorLiveData<Result<List<EventEntity>>>()
        result.value = Result.Loading
        val localData = eventDao.getFinishedEvents()
        result.addSource(localData) { newData: List<EventEntity> ->
            result.value = Result.Success(newData)
        }
        return result
    }

    fun searchUpcomingEvents(query: String): LiveData<Result<List<EventEntity>>> {
        val result = MediatorLiveData<Result<List<EventEntity>>>()
        result.value = Result.Loading
        val localData = eventDao.searchUpcomingEvents(query)
        result.addSource(localData) { newData: List<EventEntity> ->
            result.value = Result.Success(newData)
        }
        return result
    }

    fun searchFinishedEvents(query: String): LiveData<Result<List<EventEntity>>> {
        val result = MediatorLiveData<Result<List<EventEntity>>>()
        result.value = Result.Loading
        val localData = eventDao.searchFinishedEvents(query)
        result.addSource(localData) { newData: List<EventEntity> ->
            result.value = Result.Success(newData)
        }
        return result
    }

    fun getFavoriteEvents(): LiveData<Result<List<EventEntity>>> {
        val result = MediatorLiveData<Result<List<EventEntity>>>()
        result.value = Result.Loading
        val localData = eventDao.getFavoriteEvents()
        result.addSource(localData) { newData: List<EventEntity> ->
            result.value = Result.Success(newData)
        }
        return result
    }

    fun getEventById(eventId: Int): LiveData<EventEntity> {
        return eventDao.getEventById(eventId)
    }

    fun setFavoriteEvent(eventId: Int, isFavorite: Boolean) {
        appExecutors.diskIO.execute {
            eventDao.setFavoriteEvent(eventId, isFavorite)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkEventActive(beginTime: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val current = LocalDateTime.now().format(formatter)
        return LocalDateTime.parse(current, formatter) < LocalDateTime.parse(beginTime, formatter)
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null
        fun getInstance(
            apiService: ApiService,
            eventDao: EventDao,
            appExecutors: AppExecutors
        ) : EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService, eventDao, appExecutors)
            }.also { instance = it }
    }
}