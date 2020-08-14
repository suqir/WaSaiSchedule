package com.suqir.wasaischedule.logic.model

import com.google.gson.annotations.SerializedName

/**
 * Author: Suqir
 * Date: 2020/8/13 17:34
 * Desc: AccessTokenResponse
 **/
data class AccessTokenResponse(val code: Int, val msg: String, val data: AccessToken) {
    data class AccessToken(@SerializedName("access_token") val accessToken: String)
}