package com.suqir.wasaischedule.ui.weike_life.score

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.suqir.wasaischedule.R
import com.suqir.wasaischedule.logic.model.StudentScoreResponse

/**
 * Author: Suqir
 * Date: 2020/8/15 16:48
 * Desc:
 **/
class StudentScoreAdapter(layoutResId: Int, data: MutableList<StudentScoreResponse.ScoreItem>) : BaseQuickAdapter<StudentScoreResponse.ScoreItem, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder, item: StudentScoreResponse.ScoreItem?) {
        if (item == null) return
        helper.setText(R.id.tv_teacher, item.kcmc)
        helper.setText(R.id.tv_gh, item.kccj)
    }
}