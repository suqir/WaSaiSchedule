package com.suqir.wasaischedule.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.suqir.wasaischedule.BuildConfig
import com.suqir.wasaischedule.R
import com.suqir.wasaischedule.ui.base_view.BaseDialogFragment
import com.suqir.wasaischedule.ui.donate.DonateActivity
import com.suqir.wasaischedule.utils.CourseUtils
import com.suqir.wasaischedule.utils.DonateUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_donate.*
import splitties.activities.start
import java.util.*

class DonateFragment : BaseDialogFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_donate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        if (BuildConfig.CHANNEL == "google" || BuildConfig.CHANNEL == "huawei") {
            tv_donate.visibility = View.GONE
        }
        if (BuildConfig.CHANNEL == "huawei") {
            tv_donate_list.visibility = View.GONE
        }
    }

    private fun initEvent() {
        ib_close.setOnClickListener {
            dismiss()
        }

        tv_weibo.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse("sinaweibo://userinfo?uid=6462953618")
                requireActivity().startActivity(intent)
            } catch (e: Exception) {
                Toasty.info(requireContext().applicationContext, "没有找到微博客户端").show()
            }
        }

//        tv_star.setOnClickListener {
//            try {
//                val uri = Uri.parse("market://details?id=com.suqir.wasaischedule")
//                val intent = Intent(Intent.ACTION_VIEW, uri)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                requireActivity().startActivity(intent)
//            } catch (e: Exception) {
//                Toasty.info(requireContext().applicationContext, "没有找到手机上的应用商店").show()
//            }
//        }

        tv_donate.setOnClickListener {
            if (BuildConfig.CHANNEL != "google") {
                if (DonateUtils.isAppInstalled(requireContext().applicationContext, "com.eg.android.AlipayGphone")) {
                    val intent = Intent()
                    intent.action = "android.intent.action.VIEW"
                    val qrCodeUrl = Uri.parse("alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=HTTPS://QR.ALIPAY.COM/fkx10155ru1xg5tv0xbks20?_s=web-other")
                    intent.data = qrCodeUrl
                    intent.setClassName("com.eg.android.AlipayGphone", "com.alipay.mobile.quinox.LauncherActivity")
                    startActivity(intent)
                    Toasty.success(requireContext().applicationContext, "非常感谢(*^▽^*)").show()
                } else {
                    Toasty.info(requireContext().applicationContext, "没有检测到支付宝客户端o(╥﹏╥)o").show()
                }
            }
        }

        tv_feedback.setOnClickListener {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            if (hour !in 8..21) {
                Toasty.info(requireActivity().applicationContext, "开发者在休息哦zZ\n请换个时间反馈吧").show()
            } else {
                if (CourseUtils.isQQClientAvailable(requireContext().applicationContext)) {
                    val qqUrl = "mqqwpa://im/chat?chat_type=wpa&uin=977265215&version=1"
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(qqUrl)))
                } else {
                    Toasty.info(requireContext().applicationContext, "手机上没有安装QQ，无法启动聊天窗口:-(", Toast.LENGTH_LONG).show()
                }
            }
        }

        tv_donate_list.setOnClickListener {
            requireActivity().start<DonateActivity>()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DonateFragment()
    }
}
