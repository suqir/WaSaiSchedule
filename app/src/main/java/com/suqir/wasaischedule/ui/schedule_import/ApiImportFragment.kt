package com.suqir.wasaischedule.ui.schedule_import

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputLayout
import com.suqir.wasaischedule.R
import com.suqir.wasaischedule.ui.base_view.BaseFragment
import es.dmoral.toasty.Toasty
import jahirfiquitiva.libs.textdrawable.TextDrawable
import kotlinx.android.synthetic.main.fragment_import_api.*
import kotlinx.coroutines.delay
import splitties.dimensions.dip

/**
 * Author: Suqir
 * Date: 2020/8/12 14:46
 * Desc:
 **/
class ApiImportFragment : BaseFragment() {

    var wkId = ""
    var year = ""
    var term = ""

    private val viewModel by activityViewModels<ImportViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_import_api, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_title.text = viewModel.school
        if (viewModel.school == "潍坊科技学院-教师专用") {
            input_id.hint = "工号"
        }
        initEvent()

    }

    private fun initEvent() {

        val textDrawable = TextDrawable
                .builder()
                .textColor(Color.WHITE)
                .fontSize(requireContext().dip(24))
                .useFont(ResourcesCompat.getFont(requireContext(), R.font.iconfont)!!)
                .buildRect("\uE6DE", Color.TRANSPARENT)

        fab_submit.setImageDrawable(textDrawable)

        fab_submit.setOnClickListener { fab_submit ->
            when {
                et_id.text!!.isEmpty() -> input_id.showError("学号不能为空")
                et_year.text!!.isEmpty() -> input_year.showError("学年不能为空")
                et_term.text!!.isEmpty() -> input_term.showError("学期不能为空")
                viewModel.school == "潍坊科技学院-教师专用" -> launch { getTeacherSchedule() }
                else -> launch { getStudentSchedule() }
            }
        }
    }

    private fun getStudentSchedule() {
        hideIM()
        pb_loading.visibility = View.VISIBLE
        fab_submit.visibility = View.INVISIBLE
        wkId = et_id.text.toString().trim()
        year = et_year.text.toString().trim()
        term = et_term.text.toString().trim()
        viewModel.importStudentSchedule(requireContext(), wkId, year, term).observe(viewLifecycleOwner, Observer { result ->
            val rowNum = result.getOrNull()
            if (rowNum != null) {
                showSuccess(rowNum)
            } else {
                showError("请检查信息是否有误")
            }
        })
    }

    private fun getTeacherSchedule() {
        hideIM()
        pb_loading.visibility = View.VISIBLE
        fab_submit.visibility = View.INVISIBLE
        wkId = et_id.text.toString().trim()
        year = et_year.text.toString().trim()
        term = et_term.text.toString().trim()
        viewModel.importTeacherSchedule(requireContext(), wkId, year, term).observe(viewLifecycleOwner, Observer { result ->
            val rowNum = result.getOrNull()
            if (rowNum != null) {
                showSuccess(rowNum)
            } else {
                showError("请检查信息是否有误")
            }
        })
    }

    private fun hideIM() {
        val im = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(et_term.windowToken, 0)
    }

    private fun showSuccess(result: Int) {
        val intent = Intent().apply { putExtra("course_total", result) }
        requireActivity().setResult(Activity.RESULT_OK, intent)
        requireActivity().finish()
    }

    private fun showError(errMsg: String) {
        pb_loading.visibility = View.INVISIBLE
        fab_submit.visibility = View.VISIBLE
        Toasty.error(requireActivity(),
                "导入失败\n${errMsg}", Toast.LENGTH_LONG).show()
    }

    private fun TextInputLayout.showError(str: String, dur: Long = 3000) {
        launch {
            this@showError.error = str
            delay(dur)
            this@showError.error = null
        }
    }
}