package com.dicoding.dicodingevent.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.dicodingevent.data.response.DetailEventResponse
import com.dicoding.dicodingevent.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel : ViewModel() {
    private val _eventDetail = MutableLiveData<DetailEventResponse>()
    val eventDetail: LiveData<DetailEventResponse> = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    companion object {
        private const val TAG = "DetailActivity"
    }

    fun findDetailEvent(eventId: Int) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetailEvent(eventId.toString())
        client.enqueue(object : Callback<DetailEventResponse> {
            override fun onResponse(call: Call<DetailEventResponse>, response: Response<DetailEventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _eventDetail.value = response.body()
                    _errorMessage.value = null
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    _errorMessage.value = "Gagal memuat data: Data tidak ditemukan atau tidak ada koneksi internet"
                }
            }

            override fun onFailure(call: Call<DetailEventResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
                _errorMessage.value = "Gagal memuat data: Data tidak ditemukan atau tidak ada koneksi internet"
            }
        })
    }
}