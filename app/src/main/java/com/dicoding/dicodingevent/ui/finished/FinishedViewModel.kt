package com.dicoding.dicodingevent.ui.finished

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.dicodingevent.data.response.EventResponse
import com.dicoding.dicodingevent.data.response.ListEventsItem
import com.dicoding.dicodingevent.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FinishedViewModel : ViewModel() {

    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents: LiveData<List<ListEventsItem>> = _finishedEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    companion object {
        private const val TAG = "HomeActivity"
    }

    init {
        findEvents()
    }

    private fun findEvents() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getEvents(0)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _finishedEvents.value = response.body()?.listEvents?.filterNotNull() ?: emptyList()
                    _errorMessage.value = null
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    _errorMessage.value = "Gagal memuat data: Data tidak ditemukan atau tidak ada koneksi internet"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
                _errorMessage.value = "Gagal memuat data: Data tidak ditemukan atau tidak ada koneksi internet"
            }
        })
    }

    fun searchEvents(query: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().searchEvents(0, query)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _finishedEvents.value = response.body()?.listEvents?.filterNotNull() ?: emptyList()
                    _errorMessage.value = null
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    _errorMessage.value = "Gagal memuat data: Data tidak ditemukan atau tidak ada koneksi internet"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
                _errorMessage.value = "Gagal memuat data: Data tidak ditemukan atau tidak ada koneksi internet"
            }
        })
    }
}