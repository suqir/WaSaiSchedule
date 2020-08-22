package com.suqir.wasaischedule.logic.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UpdateInfoResponse(val status: String, val data: UpdateInfo) : Parcelable {
    @Parcelize
    data class UpdateInfo(
            val version_code: Int,
            val version_name: String,
            val version_info: String,
            val download_url: String
    ) : Parcelable
}