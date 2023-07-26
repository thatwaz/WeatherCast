package com.thatwaz.weathercast.view.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.thatwaz.weathercast.R
import com.thatwaz.weathercast.databinding.FragmentHourlyBinding


class HourlyFragment : Fragment() {

    private lateinit var bottomNavView: BottomNavigationView

    private var _binding: FragmentHourlyBinding ? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bottomNavView = activity?.findViewById(R.id.bnv_weather_cast) ?: return binding.root
        // Inflate the layout for this fragment
        _binding = FragmentHourlyBinding.inflate(inflater,container,false)
        return binding.root
    }


}