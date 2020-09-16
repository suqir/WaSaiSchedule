package com.suqir.wasaischedule

import android.annotation.TargetApi
import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import com.suqir.wasaischedule.ui.schedule_settings.ScheduleSettingsActivity
import com.suqir.wasaischedule.utils.Const
import com.suqir.wasaischedule.utils.getPrefer
import com.tencent.bugly.Bugly
import com.tencent.bugly.Bugly.applicationContext
import com.tencent.bugly.beta.Beta
import com.tencent.tinker.entry.DefaultApplicationLike
import es.dmoral.toasty.Toasty

/**
 * Author: Suqir
 * Date: 2020/9/16 14:40
 * Desc:
 **/
class SampleApplicationLike(application: Application, tinkerFlags: Int, tinkerLoadVerifyFlag: Boolean, applicationStartElapsedTime: Long, applicationStartMillisTime: Long, tinkerResultIntent: Intent) : DefaultApplicationLike(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent) {


    var activityCount = 0

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = application.applicationContext
        Toasty.Config.getInstance()
                .setToastTypeface(Typeface.DEFAULT_BOLD)
                .setTextSize(12)
                .apply()
        buglyInit()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var channelId = "schedule_reminder"
            var channelName = "课程提醒"
            var importance = NotificationManager.IMPORTANCE_HIGH
            createNotificationChannel(application, channelId, channelName, importance)
            channelId = "news"
            channelName = "公告"
            importance = NotificationManager.IMPORTANCE_LOW
            createNotificationChannel(application, channelId, channelName, importance)
        }
        when (application.getPrefer().getInt(Const.KEY_DAY_NIGHT_THEME, 2)) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            2 -> {
                when {
                    Build.VERSION.SDK_INT >= 29 -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }
                    Build.VERSION.SDK_INT >= 23 -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                    }
                    else -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
            }
        }
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity?) {
            }

            override fun onActivityResumed(activity: Activity?) {
            }

            override fun onActivityStarted(activity: Activity?) {
                activityCount++
            }

            override fun onActivityDestroyed(activity: Activity?) {
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
                activityCount--
                if (activity is ScheduleSettingsActivity && activityCount == 0) {
                    Toasty.info(applicationContext, "对小部件的编辑需要按「返回键」退出设置页面才能生效哦", Toast.LENGTH_LONG).show()
                }
            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            }

        })
    }

    private fun buglyInit() {
        // Bugly
        // 自定义升级UI弹窗
        Beta.upgradeDialogLayoutId = R.layout.layout_upgrade

        Bugly.init(application, "3c09e9b96e", BuildConfig.DEBUG)
        Beta.autoInit = true
        Beta.autoCheckUpgrade = false
        Beta.autoDownloadOnWifi = true

    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context, channelId: String, channelName: String, importance: Int) {
        val channel = NotificationChannel(channelId, channelName, importance)
        // 允许这个渠道下的通知显示角标
        channel.setShowBadge(true)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    override fun onBaseContextAttached(base: Context?) {
        super.onBaseContextAttached(base)
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base)

        // 安装tinker
        Beta.installTinker(this)
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    fun registerActivityLifecycleCallback(callbacks: ActivityLifecycleCallbacks?) {
        application.registerActivityLifecycleCallbacks(callbacks)
    }

}