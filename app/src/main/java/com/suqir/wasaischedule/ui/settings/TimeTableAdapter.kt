package com.suqir.wasaischedule.ui.settings

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.suqir.wasaischedule.R
import com.suqir.wasaischedule.logic.bean.TimeTableBean

class TimeTableAdapter(layoutResId: Int, data: MutableList<TimeTableBean>, var selectedId: Int) :
        BaseQuickAdapter<TimeTableBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TimeTableBean?) {
        if (item == null) return
        if (item.id == 1) {
            helper.setVisible(R.id.ib_delete, false)
        }
        if (item.id == selectedId) {
            helper.setVisible(R.id.v_selected, true)
        } else {
            helper.setVisible(R.id.v_selected, false)
        }
        helper.setText(R.id.tv_time_name, item.name)
    }
}