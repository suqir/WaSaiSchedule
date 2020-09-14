package com.suqir.wasaischedule.ui.schedule_import

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.suqir.wasaischedule.R
import com.suqir.wasaischedule.ui.base_view.BaseFragment
import com.suqir.wasaischedule.utils.Const
import com.suqir.wasaischedule.utils.Utils
import com.suqir.wasaischedule.utils.ViewUtils
import kotlinx.android.synthetic.main.fragment_excel_import.*

class ExcelImportFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_excel_import, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewUtils.resizeStatusBar(requireContext(), v_status)

        tv_template.setOnClickListener {
            Utils.openUrl(requireActivity(), "https://wwe.lanzous.com/iWAWHglz23c")
        }

        tv_self.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/*"
            }
            try {
                activity?.startActivityForResult(intent, Const.REQUEST_CODE_IMPORT_CSV)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        ib_back.setOnClickListener {
            requireActivity().finish()
        }
    }

}
