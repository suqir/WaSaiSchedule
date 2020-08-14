package com.suqir.wasaischedule.ui.settings

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.suqir.wasaischedule.R
import com.suqir.wasaischedule.logic.bean.TimeDetailBean

class TimeSettingsAdapter(layoutResId: Int, val list: MutableList<TimeDetailBean>) :
        BaseQuickAdapter<TimeDetailBean, BaseViewHolder>(layoutResId, list) {

    override fun convert(helper: BaseViewHolder, item: TimeDetailBean?) {
        if (item == null) return
        val name = "第 ${item.node} 节"
        helper.setText(R.id.tv_time_name, name)
        helper.setText(R.id.tv_start, item.startTime)
        helper.setText(R.id.tv_end, item.endTime)
    }
}