package com.suqir.wasaischedule.utils

import android.content.Context

object DonateUtils {

    fun isAppInstalled(context: Context, pkgName: String): Boolean {
        val packageManager = context.packageManager
        val pInfo = packageManager.getInstalledPackages(0)
        if (pInfo != null) {
            for (i in pInfo.indices) {
                val pn = pInfo[i].packageName
                if (pn == pkgName) {
                    return true
                }
            }
        }
        return false
    }
}