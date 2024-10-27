package com.dicoding.dicodingevent.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.dicoding.dicodingevent.data.response.Event
import com.dicoding.dicodingevent.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val id = intent.getIntExtra(EXTRA_ID, 0)
        detailViewModel.findDetailEvent(id)

        detailViewModel.eventDetail.observe(this) {
            setDetailEventData(it.event)
        }

        detailViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        detailViewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                showError(it)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDetailEventData(event: Event?) {
        Glide.with(this@DetailActivity)
            .load(event?.mediaCover)
            .into(binding.imgMediaCover)
        binding.apply {
            tvName.text = event?.name
            tvSummary.text = event?.summary
            tvOwnerName.text = "Penyelenggara: ${event?.ownerName}"
            tvQuota.text = "Sisa Kuota ${event?.quota?.minus(event.registrants!!)}"
            tvBeginTime.text = event?.beginTime
            tvDescription.text = HtmlCompat.fromHtml(
                event?.description.toString(),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            btnRegister.setOnClickListener {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(event?.link))
                startActivity(webIntent)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) = binding.progressBar.isVisible == isLoading

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}