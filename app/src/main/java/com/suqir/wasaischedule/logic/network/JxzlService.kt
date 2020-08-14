package com.suqir.wasaischedule.logic.network

import com.suqir.wasaischedule.logic.model.AccessTokenResponse
import com.suqir.wasaischedule.logic.model.TeachersResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Author: Suqir
 * Date: 2020/8/13 17:01
 * Desc:
 **/
interface JxzlService {

    @POST("app/student/getAccessToken")
    @FormUrlEncoded
    fun getAccessToken(@Field("username") username: String): Call<AccessTokenResponse>

    @POST("app/student/getStudentInfo")
    @FormUrlEncoded
    fun getStudentInfo(@Field("access_token") accessToken: String): Call<ResponseBody>

    @POST("app/student/getTeachers")
    @FormUrlEncoded
    fun getTeachers(@Field("searchName") searchName: String, @Field("access_token") accessToken: String): Call<TeachersResponse>
}