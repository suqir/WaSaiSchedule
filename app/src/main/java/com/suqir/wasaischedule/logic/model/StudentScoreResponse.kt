package com.suqir.wasaischedule.logic.model

/**
 * Author: Suqir
 * Date: 2020/8/9 10:44
 * Desc: 潍坊科技学院学生成绩Response
 **/
data class StudentScoreResponse(
        val msg: String,
        val total: Int,
        val pageCount: Int,
        val curPage: Int,
        val totalPages: Int,
        val list: List<ScoreItem>
) {
    data class ScoreItem(
            val kccj: String,
            val kch: String,
            val ksfs: String,
            val xf: Float,
            val xh: String,
            val xn: String,
            val xq: String,
            val kcmc: String,
            val xm: String
    )
}