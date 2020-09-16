package com.suqir.wasaischedule.logic.network

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Author: Suqir
 * Date: 2020/8/9 11:54
 * Desc: 统一的网络数据源访问入口，对所有网络请求的API进行封装
 **/
object WaSaiNetwork {
    private val scheduleService = ServiceCreator.create<ScheduleService>()
    private val wkService = ServiceCreator.wkCreate<WKService>()
    private val jxzlService = ServiceCreator.jxzlCreate<JxzlService>()

    // WaSaiSchedule部分
    suspend fun getNoticeInfo() = scheduleService.getNoticeInfo().await()
    suspend fun getDonateList() = scheduleService.getDonateList().await()
    suspend fun postHtml(school: String, type: String, html: String, qq: String) = scheduleService.postHtml(school, type, html, qq).await()
    suspend fun addHtml(school: String, type: String, html: String, qq: String, count: Int) = scheduleService.addHtml(school, type, html, qq, count).await()
    suspend fun getApplySchool() = scheduleService.getApplySchool().await()

    // 潍坊科技学院专用
    suspend fun getStudentSchedule(xh: String, xn: String, xq: String, offset: String) = wkService.getStudentSchedule(xh, xn, xq, offset).await()
    suspend fun getTeacherSchedule(gh: String, xn: String, xq: String, offset: String) = wkService.getTeacherSchedule(gh, xn, xq, offset).await()
    suspend fun getStudentScore(xh: String, xn: String, xq: String, offset: String) = wkService.getStudentScore(xh, xn, xq, offset).await()
    suspend fun getYktRecord(xgh: String, offset: String) = wkService.getYktRecord(xgh, "30", offset).await()

    suspend fun getAccessToken(userName: String) = jxzlService.getAccessToken(userName).await()
    suspend fun getTeachers(searchName: String, accessToken: String) = jxzlService.getTeachers(searchName, accessToken).await()

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                    Log.d("TAG", "onFailure: ${t.message}")
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) {
                        Log.d("TAG", "onResponse: $body")
                        continuation.resume(body)
                    } else {
                        continuation.resumeWithException(RuntimeException("response is null"))
                    }
                }

            })
        }
    }
}
