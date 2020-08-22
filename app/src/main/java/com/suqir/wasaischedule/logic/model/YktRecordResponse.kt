package com.suqir.wasaischedule.logic.model

import com.google.gson.annotations.SerializedName

/**
 * Author: Suqir
 * Date: 2020/8/9 11:34
 * Desc: 潍坊科技学院一卡通消费记录Response
 **/
data class YktRecordResponse(val msg: String,
                             val total: Int,
                             val pageCount: Int,
                             val curPage: Int,
                             val totalPages: Int,
                             val list: List<Order>
) {
    data class Order(
            @SerializedName("CUSTOMERID") val customerId: Long,
            @SerializedName("DSCRP") val desc: String,
            @SerializedName("ODDFARE") val oddFare: Float,
            @SerializedName("OPFARE") val opFare: Float,
            @SerializedName("OPDT") val opDate: Long,
            @SerializedName("OUTID") val outId: String
    )
}