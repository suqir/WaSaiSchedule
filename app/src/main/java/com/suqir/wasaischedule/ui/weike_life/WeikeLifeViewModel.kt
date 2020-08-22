package com.suqir.wasaischedule.ui.weike_life

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.suqir.wasaischedule.logic.Repository
import com.suqir.wasaischedule.logic.model.StudentScoreResponse
import com.suqir.wasaischedule.logic.model.TeachersResponse
import com.suqir.wasaischedule.logic.model.YktRecordResponse

/**
 * Author: Suqir
 * Date: 2020/8/14 17:50
 * Desc:
 **/
class WeikeLifeViewModel : ViewModel() {

    var curPage = 1

    var studentId = ""

    var maxPage = 10

    private var studentInfo = MutableLiveData<List<String>>()

    private val queryText = MutableLiveData<String>()

    private val yktQuery = MutableLiveData<List<String>>()

    var teacherList = arrayListOf<TeachersResponse.Teacher>()

    var scoreList = arrayListOf<StudentScoreResponse.ScoreItem>()

    var recordList = arrayListOf<YktRecordResponse.Order>()

    val studentScoreLiveData = Transformations.switchMap(studentInfo) { infoList ->
        Repository.getStudentScore(infoList[0], infoList[1], infoList[2])
    }

    val teachersLiveData = Transformations.switchMap(queryText) { query ->
        Repository.getTeachersLiveData(query)
    }

    val yktRecordLiveData = Transformations.switchMap(yktQuery) { infoList ->
        Repository.getYktRecordLiveData(infoList[0], infoList[1])
    }

    fun setStudentInfo(xh: String, xn: String, xq: String) {
        studentInfo.value = arrayListOf(xh, xn, xq)
    }

    fun setQueryText(query: String) {
        queryText.value = query
    }

    fun setYktQuery(xgh: String, offset: String) {
        yktQuery.value = arrayListOf(xgh, offset)
    }

}