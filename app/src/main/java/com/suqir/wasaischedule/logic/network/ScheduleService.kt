package com.suqir.wasaischedule.logic.network

import com.suqir.wasaischedule.logic.model.UpdateInfoResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HTTP

/**
 * Author: Suqir
 * Date: 2020/8/9 10:22
 * Desc: 哇噻课程表API接口
 **/
interface ScheduleService {
    @GET("count")
    fun addCount(): Call<ResponseBody>

    @GET("getDonate")
    fun getDonateList(): Call<ResponseBody>

    @GET("getUpdate")
    fun getUpdateInfo(): Call<UpdateInfoResponse>

    @GET("count_html")
    fun getHtmlCount(): Call<ResponseBody>

    @HTTP(method = "POST", path = "apply_html", hasBody = true)
    @FormUrlEncoded
    fun postHtml(@Field("school") school: String,
                 @Field("type") type: String,
                 @Field("html") html: String,
                 @Field("qq") qq: String
    ): Call<ResponseBody>
}