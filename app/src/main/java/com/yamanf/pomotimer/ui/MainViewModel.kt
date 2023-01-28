package com.yamanf.pomotimer.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class MainViewModel: ViewModel() {

    private val _remainingMinutes = MutableLiveData<Int>()
    val remainingMinutes: LiveData<Int>
        get() = _remainingMinutes

    private val _remainingSeconds = MutableLiveData<Int>()
    val remainingSeconds: LiveData<Int>
        get() = _remainingSeconds

    private val _isCounting = MutableLiveData<Boolean>()
    val isCounting: LiveData<Boolean>
        get() = _isCounting

    private val _isPaused = MutableLiveData<Boolean>()
    val isPaused: LiveData<Boolean>
        get() = _isPaused

    private val _isFinished = MutableLiveData<Boolean>()
    val isFinished: LiveData<Boolean>
        get() = _isFinished

    private var remainingTime: Int = 0
    private var countdownJob: Job? = null

    private val _sharedPref = MutableLiveData<SharedPreferences>()
    val sharedPref: LiveData<SharedPreferences>
        get() = _sharedPref



    init {
        _isCounting.value = false
        _isPaused.value = false
        _isFinished.value = false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun vibrate(context:Context,time:Long){
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val effect = VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(effect)
    }


    fun startCountdown(minutes: Int, seconds: Int) {
        _isCounting.value = true
        _isFinished.value = false
        remainingTime = minutes * 60 + seconds
        countdownJob?.cancel()
        countdownJob = GlobalScope.launch {
            for (i in remainingTime downTo 1) {
                if (isActive) {
                    val remainingMinutes = i / 60
                    val remainingSeconds = i % 60
                    _remainingMinutes.postValue(remainingMinutes)
                    _remainingSeconds.postValue(remainingSeconds)
                    delay(1000)
                } else {
                    return@launch
                }
            }
            _remainingMinutes.postValue(0)
            _remainingSeconds.postValue(0)
            _isFinished.postValue(true)
        }
    }

    fun resetCountdown(minutes: Int, seconds: Int){
        _isCounting.value = false
        _isPaused.value = false
        countdownJob?.cancel()
        _remainingMinutes.postValue(minutes)
        _remainingSeconds.postValue(seconds)
        remainingTime = 0
        startCountdown(minutes,seconds)
        pauseCountdown()
    }

    fun pauseCountdown() {
        countdownJob?.let {
            if (it.isActive) {
                _isPaused.value = true
                it.cancel()
            }
        }
    }

    fun stopCountdown() {
        _isCounting.value = false
        _isFinished.value = true
        _isPaused.value = false
        countdownJob?.cancel()
        _remainingMinutes.postValue(0)
        _remainingSeconds.postValue(0)
        remainingTime = 0
    }

    fun continueCountdown() {
        if (remainingTime > 0) {
            _isPaused.value = false
            startCountdown(_remainingMinutes.value!!, _remainingSeconds.value!! % 60)
        }
    }

}