package com.dicoding.dicodingevent.ui.upcoming

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.dicodingevent.data.Result
import com.dicoding.dicodingevent.data.local.room.EventDatabase
import com.dicoding.dicodingevent.data.remote.response.ListEventsItem
import com.dicoding.dicodingevent.data.remote.retrofit.ApiConfig
import com.dicoding.dicodingevent.data.repository.EventRepository
import com.dicoding.dicodingevent.databinding.FragmentUpcomingBinding
import com.dicoding.dicodingevent.ui.detail.DetailActivity
import com.dicoding.dicodingevent.ui.EventAdapter
import com.dicoding.dicodingevent.ui.EventViewModel
import com.dicoding.dicodingevent.ui.ViewModelFactory
import com.dicoding.dicodingevent.utils.AppExecutors

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("View binding is only valid between onCreateView and onDestroyView")

    private val eventViewModel: EventViewModel by viewModels {
        ViewModelFactory(
            EventRepository.getInstance(
            ApiConfig.getApiService(),
            EventDatabase.getDatabase(requireContext()).eventDao(),
            AppExecutors()
        ))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        super.onCreate(savedInstanceState)

        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvEvent.layoutManager = layoutManager

        eventViewModel.getUpcomingEvents().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    val eventList = result.data.map { eventEntity ->
                        ListEventsItem(
                            id = eventEntity.id,
                            name = eventEntity.name,
                            summary = eventEntity.summary,
                            mediaCover = eventEntity.mediaCover,
                            registrants = eventEntity.registrants,
                            imageLogo = eventEntity.imageLogo,
                            link = eventEntity.link,
                            description = eventEntity.description,
                            ownerName = eventEntity.ownerName,
                            cityName = eventEntity.cityName,
                            quota = eventEntity.quota,
                            beginTime = eventEntity.beginTime,
                            endTime = eventEntity.endTime,
                            category = eventEntity.category
                        )
                    }
                    setEventData(eventList)
                }
                is Result.Error -> {
                    showLoading(false)
                    showError(result.error)
                }
            }
        }

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { _, _, _ ->
                    searchBar.setText(searchView.text)
                    searchView.hide()
                    eventViewModel.searchUpcomingEvents(searchView.text.toString()).observe(viewLifecycleOwner) { result ->
                        when (result) {
                            is Result.Loading -> showLoading(true)
                            is Result.Success -> {
                                showLoading(false)
                                val eventList = result.data.map { eventEntity ->
                                    ListEventsItem(
                                        id = eventEntity.id,
                                        name = eventEntity.name,
                                        summary = eventEntity.summary,
                                        mediaCover = eventEntity.mediaCover,
                                        registrants = eventEntity.registrants,
                                        imageLogo = eventEntity.imageLogo,
                                        link = eventEntity.link,
                                        description = eventEntity.description,
                                        ownerName = eventEntity.ownerName,
                                        cityName = eventEntity.cityName,
                                        quota = eventEntity.quota,
                                        beginTime = eventEntity.beginTime,
                                        endTime = eventEntity.endTime,
                                        category = eventEntity.category
                                    )
                                }
                                setEventData(eventList)
                            }
                            is Result.Error -> {
                                showLoading(false)
                                showError(result.error)
                            }
                        }
                    }
                    false
                }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setEventData(event: List<ListEventsItem?>?) {
        val adapter = EventAdapter()
        adapter.submitList(event)
        binding.rvEvent.adapter = adapter

        adapter.setOnItemClickCallback(object : EventAdapter.OnItemClickCallback {
            override fun onItemClicked(event: ListEventsItem) {
                showSelectedEvent(event)
            }
        })
    }

    private fun showLoading(isLoading: Boolean) = binding.progressBar.isVisible == isLoading

    private fun showSelectedEvent(event: ListEventsItem) {
        val id = event.id
        val intent = Intent(requireActivity(), DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_ID, id)
        startActivity(intent)
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}