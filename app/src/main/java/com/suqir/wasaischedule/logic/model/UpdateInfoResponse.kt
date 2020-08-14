package com.suqir.wasaischedule.logic.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UpdateInfoResponse(
        val id: Int,
        val VersionName: String,
        val VersionInfo: String
) : Parcelable