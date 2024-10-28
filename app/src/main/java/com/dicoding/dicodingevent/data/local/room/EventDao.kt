package com.dicoding.dicodingevent.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.dicodingevent.data.local.entity.EventEntity

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEvents(events: List<EventEntity>)

    @Query("SELECT * FROM event")
    fun getAllEvents(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event WHERE isActive = 1")
    fun getUpcomingEvents(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event WHERE isActive = 0")
    fun getFinishedEvents(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event WHERE isFavorite = 1")
    fun getFavoriteEvents(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event WHERE id = :eventId")
    fun getEventById(eventId: Int): LiveData<EventEntity>

    @Query("UPDATE event SET isFavorite = :isFavorite WHERE id = :eventId")
    fun setFavoriteEvent(eventId: Int, isFavorite: Boolean)

    @Query("SELECT * FROM event WHERE isActive = 1 AND name LIKE '%' || :query || '%'")
    fun searchUpcomingEvents(query: String): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event WHERE isActive = 0 AND name LIKE '%' || :query || '%'")
    fun searchFinishedEvents(query: String): LiveData<List<EventEntity>>

    @Query("SELECT isFavorite FROM event WHERE id = :eventId")
    fun isFavorite(eventId: Int): Boolean

    @Query("DELETE FROM event")
    fun deleteAll()
}