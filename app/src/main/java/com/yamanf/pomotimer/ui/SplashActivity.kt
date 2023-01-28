package com.yamanf.pomotimer.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.yamanf.pomotimer.databinding.ActivitySplashBinding
import com.yamanf.pomotimer.utils.Constants.autoBreaks
import com.yamanf.pomotimer.utils.Constants.defaultFocusMinutes
import com.yamanf.pomotimer.utils.Constants.defaultShortBreakMinutes
import com.yamanf.pomotimer.utils.Constants.firstTime
import com.yamanf.pomotimer.utils.Constants.focusMinutes
import com.yamanf.pomotimer.utils.Constants.settings
import com.yamanf.pomotimer.utils.Constants.shortBreakMinutes

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val settings: SharedPreferences = getSharedPreferences(settings, MODE_PRIVATE)
        val isFirstTime = settings.getBoolean(firstTime, true)

        binding.splashLoadingBar.animate().setDuration(1000).alpha(1f).withEndAction() {
            if (isFirstTime) {
                val editor = settings.edit()
                editor.putBoolean(firstTime, false)
                editor.putInt(focusMinutes,defaultFocusMinutes)
                editor.putInt(shortBreakMinutes,defaultShortBreakMinutes)
                editor.putBoolean(autoBreaks, true)
                editor.apply()
                startActivity(Intent(this, MainActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }

        }


    }
}