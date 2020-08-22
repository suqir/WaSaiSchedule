package com.suqir.wasaischedule.ui.weike_life.ykt

import android.text.format.DateFormat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.suqir.wasaischedule.R
import com.suqir.wasaischedule.logic.model.YktRecordResponse
import java.util.*

/**
 * Author: Suqir
 * Date: 2020/8/17 12:57
 * Desc:
 **/
class YKTRecordAdapter(layoutResId: Int, data: MutableList<YktRecordResponse.Order>) : BaseQuickAdapter<YktRecordResponse.Order, BaseViewHolder>(layoutResId, data), LoadMoreModule {

    override fun convert(helper: BaseViewHolder, item: YktRecordResponse.Order?) {
        if (item == null) return
        helper.setText(R.id.tv_desc, item.desc)
        helper.setText(R.id.tv_date, DateFormat.format("yyyy年MM月dd日 HH:mm:ss", Date(item.opDate)))
        helper.setText(R.id.tv_op_fare, "-${item.opFare}")
        helper.setText(R.id.tv_odd_fare, "余额：${item.oddFare}")
    }

}