package com.suqir.wasaischedule.logic.model

import com.google.gson.annotations.SerializedName

/**
 * Author: Suqir
 * Date: 2020/8/13 17:13
 * Desc: 教学质量返回的老师数据
 **/
data class TeachersResponse(val code: Int, val msg: String, val data: Teachers) {
    data class Teachers(val teachers: List<Teacher>)
    data class Teacher(@SerializedName("teacher_no") val xgh: String, @SerializedName("teacher_name") val teacherName: String)
}