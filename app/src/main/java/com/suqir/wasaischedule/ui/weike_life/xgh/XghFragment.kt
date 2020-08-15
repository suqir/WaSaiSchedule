package com.suqir.wasaischedule.ui.weike_life.xgh

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.suqir.wasaischedule.R
import com.suqir.wasaischedule.ui.base_view.BaseFragment
import com.suqir.wasaischedule.ui.base_view.BaseTitleActivity
import com.suqir.wasaischedule.ui.weike_life.WeikeLifeViewModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_weike_xgh.*
import kotlinx.android.synthetic.main.item_apply_info_header.view.*
import kotlinx.android.synthetic.main.item_teacher_info.view.*
import splitties.dimensions.dip

/**
 * Author: Suqir
 * Date: 2020/8/13 18:15
 * Desc:
 **/
class XghFragment : BaseFragment() {

    private val viewModel by viewModels<WeikeLifeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as BaseTitleActivity).mainTitle.text = "教师工号"
    }

    override fun onDestroy() {
        (requireActivity() as BaseTitleActivity).mainTitle.text = "潍科生活"
        super.onDestroy()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_weike_xgh, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        xgh_recycler_view.layoutManager = LinearLayoutManager(requireContext())
        xgh_recycler_view.adapter = XghAdapter(R.layout.item_teacher_info, viewModel.teacherList).apply {
            setFooterView(initFooterView())
            setOnItemLongClickListener { _, view, _ ->
                val gh = view.tv_gh.text.toString().trim()
                val teacher = view.tv_teacher.text.toString().trim()
                copy("$teacher:$gh")
                Toasty.info(requireContext(), "已将复制到剪切板", Toasty.LENGTH_SHORT).show()
                true
            }
        }
        initEvent()
        viewModel.teachersLiveData.observe(viewLifecycleOwner, Observer { result ->
            val list = result.getOrNull()
            if (list != null) {
                viewModel.teacherList.clear()
                viewModel.teacherList.addAll(list)
                xgh_recycler_view.adapter?.notifyDataSetChanged()
            }
        })
    }

    private fun initEvent() {
        viewModel.setQueryText("")
        et_xgh.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                viewModel.setQueryText(text.toString())
            }
        }
    }

    private fun copy(str: String) {
        // 获取系统剪贴板
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
        val clipData = ClipData.newPlainText(null, str)
        // 把数据集设置（复制）到剪贴板
        clipboard.setPrimaryClip(clipData)
    }

    private fun initFooterView(): View {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.item_apply_info_header, null)
        view.setPadding(0, 0, 0, requireActivity().dip(8))
        view.tips.text = "已经加载完毕了"
        return view
    }
}