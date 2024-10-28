package com.dicoding.dicodingevent.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.dicodingevent.data.Result
import com.dicoding.dicodingevent.data.local.room.EventDatabase
import com.dicoding.dicodingevent.data.remote.response.ListEventsItem
import com.dicoding.dicodingevent.data.remote.retrofit.ApiConfig
import com.dicoding.dicodingevent.data.repository.EventRepository
import com.dicoding.dicodingevent.databinding.FragmentHomeBinding
import com.dicoding.dicodingevent.ui.DetailActivity
import com.dicoding.dicodingevent.ui.EventAdapter
import com.dicoding.dicodingevent.ui.EventViewModel
import com.dicoding.dicodingevent.ui.ViewModelFactory
import com.dicoding.dicodingevent.utils.AppExecutors

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        super.onCreate(savedInstanceState)

        val upcomingEventslayoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvUpcomingEvents.layoutManager = upcomingEventslayoutManager

        val finishedEventsLayoutManager = LinearLayoutManager(requireActivity())
        binding.rvFinishedEvents.layoutManager = finishedEventsLayoutManager

        eventViewModel.getUpcomingEvents().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> showLoadingUpcomingEvents(true)
                is Result.Success -> {
                    showLoadingUpcomingEvents(false)
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
                    setUpcomingEventData(eventList)
                }
                is Result.Error -> {
                    showLoadingUpcomingEvents(false)
                    showError(result.error)
                }
            }
        }

        eventViewModel.getFinishedEvents().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> showLoadingFinishedEvents(true)
                is Result.Success -> {
                    showLoadingFinishedEvents(false)
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
                    setFinishedEventData(eventList)
                }
                is Result.Error -> {
                    showLoadingFinishedEvents(false)
                    showError(result.error)
                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpcomingEventData(event: List<ListEventsItem?>?) {
        val adapter = EventAdapter()
        adapter.submitList(event?.take(5))
        binding.rvUpcomingEvents.adapter = adapter

        adapter.setOnItemClickCallback(object : EventAdapter.OnItemClickCallback {
            override fun onItemClicked(event: ListEventsItem) {
                showSelectedEvent(event)
            }
        })
    }

    private fun setFinishedEventData(event: List<ListEventsItem?>?) {
        val adapter = EventAdapter()
        adapter.submitList(event?.take(5))
        binding.rvFinishedEvents.adapter = adapter

        adapter.setOnItemClickCallback(object : EventAdapter.OnItemClickCallback {
            override fun onItemClicked(event: ListEventsItem) {
                showSelectedEvent(event)
            }
        })
    }

    private fun showLoadingUpcomingEvents(isLoading: Boolean) = binding.progressBarUpcomingEvents.isVisible == isLoading

    private fun showLoadingFinishedEvents(isLoading: Boolean) = binding.progressBarFinishedEvents.isVisible == isLoading

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