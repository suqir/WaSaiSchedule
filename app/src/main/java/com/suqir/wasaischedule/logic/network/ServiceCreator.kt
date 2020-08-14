package com.suqir.wasaischedule.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Author: Suqir
 * Date: 2020/8/9 10:06
 * Desc: Retrofit构建器
 **/
object ServiceCreator {

    //    private const val BASE_URL = "https://i.wakeup.fun/"
    private const val BASE_URL = "https://schdule.suqir.xyz/"
    private const val WK_BASE_URL = "http://app.wfust.edu.cn/"
    private const val WK_JXZL_URL = "http://jxzl.wfust.edu.cn/"

    private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private val wkRetrofit = Retrofit.Builder()
            .baseUrl(WK_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private val jxzlRetrofit = Retrofit.Builder()
            .baseUrl(WK_JXZL_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    fun <T> wkCreate(serviceClass: Class<T>): T = wkRetrofit.create(serviceClass)

    fun <T> jxzlCreate(serviceClass: Class<T>): T = jxzlRetrofit.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)

    inline fun <reified T> wkCreate(): T = wkCreate(T::class.java)

    inline fun <reified T> jxzlCreate(): T = jxzlCreate(T::class.java)
}