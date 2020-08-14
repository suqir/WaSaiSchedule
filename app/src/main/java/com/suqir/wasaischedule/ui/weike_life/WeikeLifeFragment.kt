package com.suqir.wasaischedule.ui.weike_life

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.suqir.wasaischedule.R
import com.suqir.wasaischedule.ui.base_view.BaseFragment
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_weike_life.*

class WeikeLifeFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_weike_life, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    private fun initEvent() {
        cv_query_score.setOnClickListener {
            Toasty.info(requireContext(), "正在开发中...", Toasty.LENGTH_LONG).show()
        }
        cv_query_xgh.setOnClickListener {
            Toasty.info(requireContext(), "正在开发中...", Toasty.LENGTH_LONG).show()
        }
        cv_query_ykt.setOnClickListener {
            Toasty.info(requireContext(), "正在开发中...", Toasty.LENGTH_LONG).show()
        }
    }

}