package com.suqir.wasaischedule.ui.weike_life.ykt

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.suqir.wasaischedule.R
import com.suqir.wasaischedule.ui.base_view.BaseFragment
import com.suqir.wasaischedule.ui.base_view.BaseTitleActivity
import com.suqir.wasaischedule.ui.weike_life.WeikeLifeViewModel
import kotlinx.android.synthetic.main.fragment_weike_ykt.*

/**
 * Author: Suqir
 * Date: 2020/8/9 13:17
 * Desc:
 **/
class YKTRecordFragment : BaseFragment() {
    private val viewModel by viewModels<WeikeLifeViewModel>()

    private lateinit var adapter: YKTRecordAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as BaseTitleActivity).mainTitle.text = "一卡通明细"
    }

    override fun onDestroy() {
        (requireActivity() as BaseTitleActivity).mainTitle.text = "潍科生活"
        super.onDestroy()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_weike_ykt, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = YKTRecordAdapter(R.layout.item_ykt_record, viewModel.recordList).apply {
            loadMoreModule?.setOnLoadMoreListener {
                viewModel.curPage++
                viewModel.setYktQuery(viewModel.studentId, viewModel.curPage.toString())
            }
        }
        ykt_recycler_view.layoutManager = LinearLayoutManager(requireContext())
        ykt_recycler_view.adapter = adapter
        initEvent()
        viewModel.yktRecordLiveData.observe(viewLifecycleOwner, Observer { result ->
            val response = result.getOrNull()
            if (response != null) {
                val list = response.list
                viewModel.maxPage = response.totalPages
                if (viewModel.curPage == 1) {
                    viewModel.recordList.clear()
                }
                viewModel.recordList.addAll(list)
                adapter.loadMoreModule?.let {
                    if (viewModel.curPage < viewModel.maxPage) {
                        it.loadMoreComplete()
                    } else {
                        it.loadMoreEnd()
                    }
                }
                adapter.setNewData(viewModel.recordList)
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun initEvent() {
        ykt_query_btn.setOnClickListener {
            hideIM()
            val studentId = et_student_id.text.toString().trim()
            if (studentId.isNotEmpty()) {
                viewModel.curPage = 1
                viewModel.studentId = studentId
                viewModel.setYktQuery(studentId, viewModel.curPage.toString())
            }
        }
    }

    private fun hideIM() {
        val im = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(et_student_id.windowToken, 0)
    }
}