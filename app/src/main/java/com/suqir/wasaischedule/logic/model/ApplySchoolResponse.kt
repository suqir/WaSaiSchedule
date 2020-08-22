package com.suqir.wasaischedule.logic.model

/**
 * Author: Suqir
 * Date: 2020/8/20 12:46
 * Desc:
 **/
data class ApplySchoolResponse(val status: String, val data: List<ApplySchool>) {
    data class ApplySchool(val school: String, val type: String, val html: String, val qq: String, val count: Int, val checked: Int, val valid: Int)
}