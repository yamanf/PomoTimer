package com.yamanf.pomotimer.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.yamanf.pomotimer.R
import com.yamanf.pomotimer.databinding.FragmentSettingsBinding
import com.yamanf.pomotimer.utils.Constants.autoBreaks
import com.yamanf.pomotimer.utils.Constants.defaultFocusMinutes
import com.yamanf.pomotimer.utils.Constants.defaultShortBreakMinutes
import com.yamanf.pomotimer.utils.Constants.focusMinutes
import com.yamanf.pomotimer.utils.Constants.settings
import com.yamanf.pomotimer.utils.Constants.shortBreakMinutes
import com.yamanf.pomotimer.utils.displayToast


class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        configureAdMob()
        val sharedPref = activity?.getSharedPreferences(settings, Context.MODE_PRIVATE)
        binding.sliderFocusTime.value = sharedPref?.getInt(focusMinutes, defaultFocusMinutes)!!.toFloat()
        binding.sliderBreakTime.value = sharedPref?.getInt(shortBreakMinutes, defaultShortBreakMinutes)!!.toFloat()
        binding.switchBreaks.isChecked = sharedPref?.getBoolean(autoBreaks, true)!!
        binding.switchBreaks.isActivated = sharedPref?.getBoolean(autoBreaks, true)!!

        binding.ivBack.setOnClickListener() {
            it.findNavController()
                .navigate(SettingsFragmentDirections.actionSettingsFragmentToFocusFragment())
        }

        binding.btnSave.setOnClickListener() {
            val newFocusMinutes = binding.sliderFocusTime.value.toInt()
            val newBreakMinutes = binding.sliderBreakTime.value.toInt()
            val newAutoBreak = binding.switchBreaks.isChecked
            println(binding.sliderFocusTime.value)
            val editor = sharedPref?.edit()
            editor?.putInt(focusMinutes, newFocusMinutes)
            editor?.putInt(shortBreakMinutes, newBreakMinutes)
            editor?.putBoolean(autoBreaks,newAutoBreak)
            editor?.apply()
            requireContext().displayToast("Settings saved!")
            it.findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToFocusFragment())
        }

        return binding.root
    }

    private fun configureAdMob() {
        MobileAds.initialize(requireContext()) {}
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

}