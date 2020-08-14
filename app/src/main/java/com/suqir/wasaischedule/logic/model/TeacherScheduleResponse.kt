package com.suqir.wasaischedule.logic.model

/**
 * Author: Suqir
 * Date: 2020/8/9 11:48
 * Desc: 潍坊科技学院教师课表Response
 **/
data class TeacherScheduleResponse(
        val msg: String,
        val total: Int,
        val pageCount: Int,
        val curPage: Int,
        val totalPage: Int,
        val list: List<TeacherCourseItem>
) {
    data class TeacherCourseItem(
            val jsxm: String,
            val kcmc: String,
            val xn: String,
            val xq: String,
            val dsz: String,
            val sksj: String,
            val skdd: String,
            val kcxz: String,
            val zc: String
    )
}