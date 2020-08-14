package com.suqir.wasaischedule.ui.schedule_import

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.suqir.wasaischedule.R
import com.suqir.wasaischedule.ui.base_view.BaseFragment
import com.suqir.wasaischedule.utils.ViewUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_school_info.*

class SchoolInfoFragment : BaseFragment() {

    private val viewModel by activityViewModels<ImportViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_school_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewUtils.resizeStatusBar(requireContext().applicationContext, v_status)
        initEvent()
    }

    private fun initEvent() {
        ib_back.setOnClickListener {
            requireActivity().finish()
        }

        chip_urp.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isUrp = isChecked
        }

        tv_next.setOnClickListener {
            if (et_school.text.toString() != "") {
                viewModel.schoolInfo[0] = et_school.text.toString()
                viewModel.schoolInfo[1] = et_type.text.toString()
                viewModel.schoolInfo[2] = et_qq.text.toString()
                val fragment = WebViewLoginFragment.newInstance()
                val transaction = parentFragmentManager.beginTransaction()
                transaction.hide(this)
                transaction.add(android.R.id.content, fragment, "webLogin")
                transaction.commit()
            } else {
                Toasty.error(requireActivity(), "请填写学校全称").show()
            }
        }
    }

}
