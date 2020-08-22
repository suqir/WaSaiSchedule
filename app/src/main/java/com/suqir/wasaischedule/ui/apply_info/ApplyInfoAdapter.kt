package com.suqir.wasaischedule.ui.apply_info

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.suqir.wasaischedule.R
import com.suqir.wasaischedule.logic.model.ApplySchoolResponse

class ApplyInfoAdapter(layoutResId: Int, data: MutableList<ApplySchoolResponse.ApplySchool>) :
        BaseQuickAdapter<ApplySchoolResponse.ApplySchool, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ApplySchoolResponse.ApplySchool?) {
        if (item == null) return
        helper.setVisible(R.id.ll_detail, true)
        helper.setVisible(R.id.ll_detail_num, true)
        helper.setVisible(R.id.v_line, true)
        helper.setText(R.id.tv_school, item.school)
        helper.setText(R.id.tv_count, item.count.toString())
        helper.setText(R.id.tv_checked, item.checked.toString())
        helper.setText(R.id.tv_valid, item.valid.toString())
    }

}