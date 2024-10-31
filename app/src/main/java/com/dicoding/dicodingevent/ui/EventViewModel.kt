package com.dicoding.dicodingevent.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.dicoding.dicodingevent.data.Result
import com.dicoding.dicodingevent.data.repository.EventRepository
import kotlinx.coroutines.launch

class EventViewModel(private val eventRepository: EventRepository) : ViewModel() {
    fun getUpcomingEvents() = eventRepository.getUpcomingEvents()

    fun getFinishedEvents() = eventRepository.getFinishedEvents()

    fun searchUpcomingEvents(query: String) = liveData {
        emit(Result.Loading)
        viewModelScope.launch {
            val result = eventRepository.searchUpcomingEvents(query)
            emitSource(result)
        }
    }

    fun searchFinishedEvents(query: String) = liveData {
        emit(Result.Loading)
        viewModelScope.launch {
            val result = eventRepository.searchFinishedEvents(query)
            emitSource(result)
        }
    }

    fun getFavoriteEvents() = eventRepository.getFavoriteEvents()
}