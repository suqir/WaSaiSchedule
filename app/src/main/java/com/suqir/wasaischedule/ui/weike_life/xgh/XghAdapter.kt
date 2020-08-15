package com.suqir.wasaischedule.ui.weike_life.xgh

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.suqir.wasaischedule.R
import com.suqir.wasaischedule.logic.model.TeachersResponse

/**
 * Author: Suqir
 * Date: 2020/8/14 18:22
 * Desc:
 **/
class XghAdapter(layoutResId: Int, data: MutableList<TeachersResponse.Teacher>) : BaseQuickAdapter<TeachersResponse.Teacher, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TeachersResponse.Teacher?) {
        if (item == null) return
        helper.setText(R.id.tv_teacher, item.teacherName)
        helper.setText(R.id.tv_gh, item.xgh)
    }

}