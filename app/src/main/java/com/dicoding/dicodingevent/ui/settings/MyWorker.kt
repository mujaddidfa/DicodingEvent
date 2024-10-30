package com.dicoding.dicodingevent.ui.settings

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dicoding.dicodingevent.BuildConfig
import com.dicoding.dicodingevent.R
import com.dicoding.dicodingevent.data.remote.response.EventResponse
import com.dicoding.dicodingevent.data.remote.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    override fun doWork(): Result {
        val call = apiService.getEventLimited(1, 1)
        call.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    val events = response.body()?.listEvents
                    events?.forEach { event ->
                        val title = event.name
                        val splitTime = event.beginTime.split(" ")
                        val date = splitTime[0]
                        val splitDate = date.split("-")
                        val month = when (splitDate[1]) {
                            "01" -> "Januari"
                            "02" -> "Februari"
                            "03" -> "Maret"
                            "04" -> "April"
                            "05" -> "Mei"
                            "06" -> "Juni"
                            "07" -> "Juli"
                            "08" -> "Agustus"
                            "09" -> "September"
                            "10" -> "Oktober"
                            "11" -> "November"
                            "12" -> "Desember"
                            else -> ""
                        }
                        val dateFormated = "${splitDate[2]} $month ${splitDate[0]}"
                        val time = splitTime[1]

                        val detail = "$dateFormated pukul $time"
                        val link = event.link
                        showNotification(title, detail, link)
                    }
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
        return Result.success()
    }

    private fun showNotification(title: String, detail: String?, link: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(link)
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_notifications_active_24)
            .setContentTitle(title)
            .setContentText(detail)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            notification.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_01"
        const val CHANNEL_NAME = "dicoding_channel"
    }
}