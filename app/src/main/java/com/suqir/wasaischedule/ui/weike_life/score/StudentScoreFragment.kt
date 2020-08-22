package com.suqir.wasaischedule.ui.weike_life.score

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputLayout
import com.suqir.wasaischedule.R
import com.suqir.wasaischedule.ui.base_view.BaseFragment
import com.suqir.wasaischedule.ui.base_view.BaseTitleActivity
import com.suqir.wasaischedule.ui.weike_life.WeikeLifeViewModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_weike_score.*
import kotlinx.android.synthetic.main.item_student_score.view.*
import kotlinx.coroutines.delay

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
        score_recycler_view.adapter = StudentScoreAdapter(R.layout.item_student_score, viewModel.scoreList).apply {
            val v = LayoutInflater.from(requireContext()).inflate(R.layout.item_student_score, null).apply {
                visibility = View.GONE
            }
            v.tv_name.text = "课程名称"
            v.tv_credit.text = "学分"
            v.tv_score.text = "成绩"
            addHeaderView(v)
        }
        viewModel.studentScoreLiveData.observe(viewLifecycleOwner, Observer { result ->
            val list = result.getOrNull()
            if (list != null) {
                Toasty.info(requireContext(), "查询成功", Toasty.LENGTH_SHORT).show()
                viewModel.scoreList.clear()
                viewModel.scoreList.addAll(list)
                score_recycler_view.adapter?.notifyDataSetChanged()
            } else {
                Toasty.error(requireContext(), "请检查信息是否有误", Toasty.LENGTH_SHORT).show()
            }
        })
        initEvent()
    }

    private fun initEvent() {
        score_query_btn.setOnClickListener {
            viewModel.scoreList.clear()
            score_recycler_view.adapter?.notifyDataSetChanged()
            val xh = et_student_id.text.toString().trim()
            val xn = et_year.text.toString().trim()
            val xq = et_term.text.toString().trim()
            when {
                xh == "" -> input_id.showError("必填项")
                xn == "" -> input_year.showError("必填项")
                xq == "" -> input_term.showError("必填项")
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

    private fun TextInputLayout.showError(str: String, dur: Long = 3000) {
        launch {
            this@showError.error = str
            delay(dur)
            this@showError.error = null
        }
    }
}