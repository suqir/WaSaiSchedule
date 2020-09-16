package com.suqir.wasaischedule.ui

import android.os.Bundle
import android.view.View
import com.suqir.wasaischedule.R
import com.suqir.wasaischedule.logic.model.NoticeResponse
import com.suqir.wasaischedule.ui.base_view.BaseDialogFragment
import kotlinx.android.synthetic.main.fragment_notice.*

class NoticeFragment : BaseDialogFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_notice

    private lateinit var noticeInfo: NoticeResponse.NoticeInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            noticeInfo = it.getParcelable("noticeInfo")!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_info.text = noticeInfo.notice_info
        tv_visit.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(arg: NoticeResponse.NoticeInfo) =
                NoticeFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("noticeInfo", arg)
                    }
                }
    }
}
