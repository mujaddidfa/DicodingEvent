package com.dicoding.dicodingevent.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.dicoding.dicodingevent.R
import com.dicoding.dicodingevent.data.local.entity.EventEntity
import com.dicoding.dicodingevent.data.local.room.EventDatabase
import com.dicoding.dicodingevent.data.remote.retrofit.ApiConfig
import com.dicoding.dicodingevent.data.repository.EventRepository
import com.dicoding.dicodingevent.databinding.ActivityDetailBinding
import com.dicoding.dicodingevent.ui.ViewModelFactory

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel: DetailViewModel by viewModels {
        ViewModelFactory(
            EventRepository.getInstance(
            ApiConfig.getApiService(),
            EventDatabase.getDatabase(this).eventDao()
        ))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val id = intent.getIntExtra(EXTRA_ID, 0)
        detailViewModel.findDetailEvent(id)

        detailViewModel.eventDetail.observe(this) { event ->
            setDetailEventData(event)
        }

        detailViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        binding.fabFavorite.setOnClickListener {
            val event = detailViewModel.eventDetail.value
            if (event != null) {
                val newState = !event.isFavorite
                detailViewModel.setFavoriteEvent(event.id, newState)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDetailEventData(event: EventEntity?) {
        Glide.with(this@DetailActivity)
            .load(event?.mediaCover)
            .into(binding.imgMediaCover)
        binding.apply {
            tvName.text = event?.name
            tvSummary.text = event?.summary
            tvOwnerName.text = "Penyelenggara: ${event?.ownerName}"
            tvQuota.text = "Sisa Kuota ${event?.quota?.minus(event.registrants)}"
            tvBeginTime.text = event?.beginTime
            tvDescription.text = HtmlCompat.fromHtml(
                event?.description.toString(),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            btnRegister.setOnClickListener {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(event?.link))
                startActivity(webIntent)
            }
            if (event?.isFavorite == true) {
                fabFavorite.setImageResource(R.drawable.baseline_favorite_24)
            } else {
                fabFavorite.setImageResource(R.drawable.baseline_favorite_border_24)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) = binding.progressBar.isVisible == isLoading

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}