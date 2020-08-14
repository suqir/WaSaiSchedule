package com.suqir.wasaischedule.logic.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UpdateInfoResponse(
        val id: Int,
        val versionName: String,
        val versionInfo: String,
        val downloadUrl: String
) : Parcelable