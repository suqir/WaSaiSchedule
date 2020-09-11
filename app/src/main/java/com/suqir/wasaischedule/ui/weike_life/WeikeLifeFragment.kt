package com.suqir.wasaischedule.ui.weike_life

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.suqir.wasaischedule.R
import com.suqir.wasaischedule.ui.base_view.BaseFragment
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_weike_life.*

class WeikeLifeFragment : BaseFragment() {

    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_weike_life, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        initEvent()
    }

    private fun initEvent() {
        cv_query_score.setOnClickListener {
            navController.navigate(R.id.action_weikeLifeFragment_to_studentScoreFragment)
        }
        cv_query_xgh.setOnClickListener {
            navController.navigate(R.id.action_weikeLifeFragment_to_xghFragment)
        }
        cv_query_ykt.setOnClickListener {
            navController.navigate(R.id.action_weikeLifeFragment_to_YKTRecordFragment)
        }
        cv_wkb.setOnClickListener {
            Toasty.info(requireContext(), "正在跳转中...", Toasty.LENGTH_LONG).show()
            val intent = Intent().apply {
                action = "android.intent.action.VIEW"
                data = Uri.parse("http://wk.suqir.xyz")
            }
            requireContext().startActivity(intent)
        }
    }

}