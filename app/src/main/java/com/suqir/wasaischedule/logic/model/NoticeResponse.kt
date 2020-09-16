package com.suqir.wasaischedule.logic.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Author: Suqir
 * Date: 2020/9/16 20:17
 * Desc:
 **/
@Parcelize
data class NoticeResponse(val status: String, val data: NoticeInfo) : Parcelable {
    @Parcelize
    data class NoticeInfo(
            val notice_id: Int,
            val notice_info: String,
    ) : Parcelable
}