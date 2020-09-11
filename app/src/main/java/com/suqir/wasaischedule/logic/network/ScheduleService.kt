package com.suqir.wasaischedule.logic.network

import com.suqir.wasaischedule.logic.model.ApplySchoolResponse
import com.suqir.wasaischedule.logic.model.DonateResponse
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

    @GET("v1/donates")
    fun getDonateList(): Call<DonateResponse>

    @GET("v1/update")
    fun getUpdateInfo(): Call<UpdateInfoResponse>

    @GET("v1/applies")
    fun getApplySchool(): Call<ApplySchoolResponse>

    @HTTP(method = "POST", path = "", hasBody = true)
    @FormUrlEncoded
    fun postHtml(@Field("school") school: String,
                 @Field("type") type: String,
                 @Field("html") html: String,
                 @Field("qq") qq: String
    ): Call<ResponseBody>

    @HTTP(method = "POST", path = "v1/apply", hasBody = true)
    @FormUrlEncoded
    fun addHtml(@Field("school") school: String,
                @Field("type") type: String,
                @Field("html") html: String,
                @Field("qq") qq: String,
                @Field("count") count: Int
    ): Call<ResponseBody>
}