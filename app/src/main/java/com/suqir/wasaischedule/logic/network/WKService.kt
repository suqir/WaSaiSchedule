package com.suqir.wasaischedule.logic.network

import com.suqir.wasaischedule.logic.model.StudentScheduleResponse
import com.suqir.wasaischedule.logic.model.StudentScoreResponse
import com.suqir.wasaischedule.logic.model.TeacherScheduleResponse
import com.suqir.wasaischedule.logic.model.YktRecordResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Author: Suqir
 * Date: 2020/8/9 10:25
 * Desc: 潍坊科技学院API接口
 **/
interface WKService {
    @GET("interface/rest/http/data_zhwk/zhwk_xskb.htm")
    fun getStudentSchedule(@Query("xh") xh: String, @Query("xn") xn: String, @Query("xq") xq: String, @Query("offset") offset: String): Call<StudentScheduleResponse>

    @GET("interface/rest/http/data_zhwk/zhwk_jskb.htm")
    fun getTeacherSchedule(@Query("gh") gh: String, @Query("xn") xn: String, @Query("xq") xq: String, @Query("offset") offset: String): Call<TeacherScheduleResponse>

    @GET("interface/rest/http/data_zhwk/zhwk_xscj.htm")
    fun getStudentScore(@Query("xh") xh: String, @Query("xn") xn: String, @Query("xq") xq: String): Call<StudentScoreResponse>

    @GET("interface/rest/http/data_zhwk/zhwk_yktxf.htm")
    fun getYktRecord(@Query("xgh") xgh: String, @Query("limit") limit: String, @Query("offset") offset: String): Call<YktRecordResponse>
}