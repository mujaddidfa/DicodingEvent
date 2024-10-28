package com.dicoding.dicodingevent.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.dicodingevent.data.local.entity.EventEntity
import com.dicoding.dicodingevent.data.repository.EventRepository

class DetailViewModel(private val eventRepository: EventRepository) : ViewModel() {
    private val _eventDetail = MutableLiveData<EventEntity>()
    val eventDetail: LiveData<EventEntity> = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun findDetailEvent(eventId: Int) {
        _isLoading.value = true
        eventRepository.getEventById(eventId).observeForever {
            _isLoading.value = false
            _eventDetail.value = it
        }
    }

    fun setFavoriteEvent(eventId: Int, isFavorite: Boolean) {
        eventRepository.setFavoriteEvent(eventId, isFavorite)
    }
}