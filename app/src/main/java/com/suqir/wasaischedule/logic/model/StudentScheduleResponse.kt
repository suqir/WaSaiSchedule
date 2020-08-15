package com.suqir.wasaischedule.logic.model

/**
 * Author: Suqir
 * Date: 2020/8/9 10:33
 * Desc: 潍坊科技学院学生课表Response
 **/
data class StudentScheduleResponse(
        val msg: String,
        val total: Int,
        val pageCount: Int,
        val curPage: Int,
        val totalPages: Int,
        val list: List<StudentCourseItem>
) {
    data class StudentCourseItem(
            val xm: String,
            val kcmc: String,
            val xn: String,
            val xq: String,
            val dsz: String,
            val sksj: String,
            val skdd: String,
            val kcxz: String,
            val zc: String,
            val xh: String,
            val jsxm: String,
            val skbj: String
    )
}