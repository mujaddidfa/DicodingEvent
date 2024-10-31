package com.dicoding.dicodingevent.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.dicodingevent.data.local.entity.EventEntity
import com.dicoding.dicodingevent.data.repository.EventRepository
import kotlinx.coroutines.launch

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
        viewModelScope.launch {
            eventRepository.setFavoriteEvent(eventId, isFavorite)
        }
    }
}