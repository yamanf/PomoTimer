package com.yamanf.pomotimer.ui

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.yamanf.pomotimer.R
import com.yamanf.pomotimer.databinding.FragmentFocusBinding
import com.yamanf.pomotimer.utils.Constants.defaultFocusMinutes
import com.yamanf.pomotimer.utils.Constants.focusMinutes
import com.yamanf.pomotimer.utils.Constants.settings
import com.yamanf.pomotimer.utils.displayToast
import com.yamanf.pomotimer.utils.gone
import com.yamanf.pomotimer.utils.visible


class FocusFragment : Fragment() {
    private var _binding: FragmentFocusBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel:MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFocusBinding.inflate(inflater, container, false)
        configureAdMob()

        val sharedPref = activity?.getSharedPreferences(settings, Context.MODE_PRIVATE)
        val focusMinutes = sharedPref?.getInt(focusMinutes, defaultFocusMinutes)
        binding.tvMinutes.text = focusMinutes.toString()
        binding.btnReset.gone()

        mainViewModel.remainingMinutes.observe(viewLifecycleOwner, Observer { remainingMinutes ->
            binding.tvMinutes.text = remainingMinutes.toString()
        })
        mainViewModel.remainingSeconds.observe(viewLifecycleOwner, Observer { remainingSeconds ->
            binding.tvSeconds.text = remainingSeconds.toString()
        })
        mainViewModel.isFinished.observe(viewLifecycleOwner, Observer { isFinished ->
            if (isFinished){
                requireView().findNavController().navigate(FocusFragmentDirections.actionFocusFragmentToBreakFragment())
            }
        })

        mainViewModel.isCounting.observe(viewLifecycleOwner, Observer { isCounting ->
            if (isCounting){
                binding.btnPause.setOnClickListener(){
                    mainViewModel.pauseCountdown()
                    binding.imgPause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                }
                binding.btnReset.visible()
            }else{
                mainViewModel.isPaused.observe(viewLifecycleOwner, Observer { isPaused ->
                    if (isPaused){
                        binding.btnPause.setOnClickListener(){
                            mainViewModel.continueCountdown()
                            binding.imgPause.setImageResource(R.drawable.ic_baseline_pause_24)
                        }
                    }else{
                        binding.btnPause.setOnClickListener(){
                            mainViewModel.startCountdown(focusMinutes!!, 0)
                            binding.btnReset.visible()
                            binding.imgPause.setImageResource(R.drawable.ic_baseline_pause_24)
                        }
                    }
                })

            }
        })



        binding.btnReset.setOnClickListener {
            resetTimer(focusMinutes!!)
        }


        binding.btnSkip.setOnClickListener(){
            mainViewModel.vibrate(requireContext(),500)
            mainViewModel.stopCountdown()
        }

        binding.btnSetting.setOnClickListener(){
        it.findNavController().navigate(FocusFragmentDirections.actionFocusFragmentToSettingsFragment())
        }


        return binding.root
    }

    private fun resetTimer(focusTime:Int) {
        val builder = AlertDialog.Builder(requireContext())
        with(builder) {
            setTitle("Reset Timer")
            setMessage("Are you sure you want to reset time?")
            setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(context, "You cancelled.", Toast.LENGTH_SHORT).show()
            }
            setPositiveButton("OK") { dialog, which ->
                mainViewModel.resetCountdown(focusTime,0)
                binding.btnReset.gone()
                binding.imgPause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun configureAdMob() {
        MobileAds.initialize(requireContext()) {}
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

}