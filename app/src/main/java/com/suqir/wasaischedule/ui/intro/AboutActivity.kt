package com.suqir.wasaischedule.ui.intro

import android.os.Bundle
import androidx.appcompat.widget.AppCompatTextView
import com.suqir.wasaischedule.BuildConfig
import com.suqir.wasaischedule.R
import com.suqir.wasaischedule.ui.base_view.BaseBlurTitleActivity
import com.suqir.wasaischedule.ui.donate.DonateActivity
import com.suqir.wasaischedule.utils.UpdateUtils
import kotlinx.android.synthetic.main.activity_about.*
import splitties.activities.start
import splitties.resources.color

class AboutActivity : BaseBlurTitleActivity() {
    override val layoutId: Int
        get() = R.layout.activity_about

    override fun onSetupSubButton(tvButton: AppCompatTextView): AppCompatTextView? {
        return if (BuildConfig.CHANNEL == "google" || BuildConfig.CHANNEL == "huawei") {
            null
        } else {
            tvButton.text = "捐赠"
            tvButton.setTextColor(color(R.color.colorAccent))
            tvButton.setOnClickListener {
                start<DonateActivity>()
            }
            tvButton
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            tv_version.text = resources.getString(R.string.version_name, UpdateUtils.getVersionName(this))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
