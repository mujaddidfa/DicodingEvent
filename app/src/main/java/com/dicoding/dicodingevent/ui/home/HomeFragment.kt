package com.dicoding.dicodingevent.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.dicodingevent.data.response.ListEventsItem
import com.dicoding.dicodingevent.databinding.FragmentHomeBinding
import com.dicoding.dicodingevent.ui.DetailActivity
import com.dicoding.dicodingevent.ui.EventAdapter

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        super.onCreate(savedInstanceState)

        val upcomingEventslayoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvUpcomingEvents.layoutManager = upcomingEventslayoutManager

        val finishedEventsLayoutManager = LinearLayoutManager(requireActivity())
        binding.rvFinishedEvents.layoutManager = finishedEventsLayoutManager

        homeViewModel.upcomingEvents.observe(viewLifecycleOwner) {
            setUpcomingEventData(it)
        }

        homeViewModel.finishedEvents.observe(viewLifecycleOwner) {
            setFinishedEventData(it)
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoadingUpcomingEvents(it)
            showLoadingFinishedEvents(it)
        }

        homeViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                showError(it)
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

    private fun showLoadingUpcomingEvents(isLoading: Boolean) {
        if (isLoading)
            binding.progressBarUpcomingEvents.visibility = View.VISIBLE
        else
            binding.progressBarUpcomingEvents.visibility = View.GONE
    }

    private fun showLoadingFinishedEvents(isLoading: Boolean) {
        if (isLoading)
            binding.progressBarFinishedEvents.visibility = View.VISIBLE
        else
            binding.progressBarFinishedEvents.visibility = View.GONE
    }

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