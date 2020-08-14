package com.suqir.wasaischedule.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.suqir.wasaischedule.R
import com.suqir.wasaischedule.logic.model.UpdateInfoResponse
import com.suqir.wasaischedule.ui.base_view.BaseDialogFragment
import com.suqir.wasaischedule.utils.UpdateUtils
import kotlinx.android.synthetic.main.fragment_update.*

class UpdateFragment : BaseDialogFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_update

    private lateinit var updateInfo: UpdateInfoResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            updateInfo = it.getParcelable("updateInfo")!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_old_version.text = "当前版本：" + UpdateUtils.getVersionName(requireContext().applicationContext)
        tv_new_version.text = "最新版本：" + updateInfo.versionName
        tv_info.text = updateInfo.versionInfo
        tv_visit.setOnClickListener {
//            if (BuildConfig.CHANNEL == "google") {
//                try {
//                    val uri = Uri.parse("market://details?id=com.suqir.wasaischedule")
//                    val intent = Intent(Intent.ACTION_VIEW, uri)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    requireActivity().startActivity(intent)
//                } catch (e: Exception) {
//                    Toasty.info(requireContext().applicationContext, "没有检测到应用商店o(╥﹏╥)o").show()
//                }
//            } else {
            val intent = Intent()
            intent.action = "android.intent.action.VIEW"
            val contentUrl = Uri.parse(updateInfo.downloadUrl)
            intent.data = contentUrl
            requireContext().startActivity(intent)
//            }
            dismiss()
        }
        ib_close.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(arg: UpdateInfoResponse) =
                UpdateFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("updateInfo", arg)
                    }
                }
    }
}
