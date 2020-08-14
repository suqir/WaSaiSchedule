package com.suqir.wasaischedule.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.suqir.wasaischedule.ui.schedule.ScheduleActivity
import com.suqir.wasaischedule.utils.UpdateUtils
import kotlinx.coroutines.launch
import splitties.activities.start

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            UpdateUtils.tranOldData(applicationContext)
            start<ScheduleActivity>()
            finish()
        }
    }

    override fun onBackPressed() {

    }
}
