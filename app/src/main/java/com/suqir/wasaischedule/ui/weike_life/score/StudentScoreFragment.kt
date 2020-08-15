package com.suqir.wasaischedule.ui.weike_life.score

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.suqir.wasaischedule.R
import com.suqir.wasaischedule.ui.base_view.BaseFragment
import com.suqir.wasaischedule.ui.base_view.BaseTitleActivity
import com.suqir.wasaischedule.ui.weike_life.WeikeLifeViewModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_weike_score.*

/**
 * Author: Suqir
 * Date: 2020/8/9 13:07
 * Desc:
 **/
class StudentScoreFragment : BaseFragment() {

    private val viewModel by viewModels<WeikeLifeViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_weike_score, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        score_recycler_view.layoutManager = LinearLayoutManager(requireContext())
        score_recycler_view.adapter = StudentScoreAdapter(R.layout.item_teacher_info, viewModel.scoreList)
        viewModel.studentScoreLiveData.observe(viewLifecycleOwner, Observer { result ->
            val list = result.getOrNull()
            if (list != null) {
                Log.d("TAG", "onViewCreated: $list")
                viewModel.scoreList.clear()
                viewModel.scoreList.addAll(list)
                score_recycler_view.adapter?.notifyDataSetChanged()
            }
        })
        initEvent()
    }

    private fun initEvent() {
        score_query_btn.setOnClickListener {
            val xh = et_student_id.text.toString().trim()
            val xn = et_year.text.toString().trim()
            val xq = et_term.text.toString().trim()
            when {
                xh == "" -> input_id.error = "必填项"
                xn == "" -> input_year.error = "必填项"
                xq == "" -> input_term.error = "必填项"
                else -> {
                    viewModel.setStudentInfo(xh, xn, xq)
                    Toasty.info(requireContext(), "正在查询...", Toasty.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as BaseTitleActivity).mainTitle.text = "学生成绩"
    }

    override fun onDestroy() {
        (requireActivity() as BaseTitleActivity).mainTitle.text = "潍科生活"
        super.onDestroy()
    }
}