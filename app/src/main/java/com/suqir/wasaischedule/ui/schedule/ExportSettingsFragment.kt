package com.suqir.wasaischedule.ui.schedule

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.suqir.wasaischedule.R
import com.suqir.wasaischedule.ui.base_view.BaseDialogFragment
import com.suqir.wasaischedule.utils.Const
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_export_settings.*

class ExportSettingsFragment : BaseDialogFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_export_settings

    private val viewModel by activityViewModels<ScheduleViewModel>()

    val tableName by lazy(LazyThreadSafetyMode.NONE) {
        if (viewModel.table.tableName == "") {
            "我的课表"
        } else {
            viewModel.table.tableName
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false

        tv_export.setOnClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/octet-stream"
                putExtra(Intent.EXTRA_TITLE, "$tableName.wakeup_schedule")
            }
            Toasty.info(requireActivity(), "请自行选择导出的地方\n不要修改文件的扩展名哦", Toasty.LENGTH_LONG).show()
            activity?.startActivityForResult(intent, Const.REQUEST_CODE_EXPORT)
            dismiss()
        }

        tv_export_ics.setOnLongClickListener {
//            Utils.openUrl(requireActivity(), "https://www.jianshu.com/p/de3524cbe8aa")
            return@setOnLongClickListener true
        }

        tv_export_ics.setOnClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/calendar"
                putExtra(Intent.EXTRA_TITLE, "日历-$tableName")
            }
            Toasty.info(requireActivity(), "请自行选择导出的地方\n不要修改文件的扩展名哦", Toasty.LENGTH_LONG).show()
            activity?.startActivityForResult(intent, Const.REQUEST_CODE_EXPORT_ICS)
            dismiss()
        }

        tv_cancel.setOnClickListener {
            dismiss()
        }
    }
}
