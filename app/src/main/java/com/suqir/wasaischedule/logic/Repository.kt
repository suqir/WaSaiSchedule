package com.suqir.wasaischedule.logic

import android.content.Context
import android.util.Log
import androidx.lifecycle.liveData
import com.suqir.wasaischedule.SampleApplicationLike.Companion.context
import com.suqir.wasaischedule.logic.bean.TableBean
import com.suqir.wasaischedule.logic.database.AppDatabase
import com.suqir.wasaischedule.logic.model.StudentScheduleResponse
import com.suqir.wasaischedule.logic.model.StudentScoreResponse
import com.suqir.wasaischedule.logic.model.TeacherScheduleResponse
import com.suqir.wasaischedule.logic.network.WaSaiNetwork
import com.suqir.wasaischedule.ui.schedule_import.parser.WfustParser
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

/**
 * Author: Suqir
 * Date: 2020/8/9 15:00
 * Desc: 外部统一调用入口
 **/
object Repository {
    private var tableName = "未命名"
    private val dataBase = AppDatabase.getDatabase(context)
    private val tableDao = dataBase.tableDao()
    private val courseDao = dataBase.courseDao()

    fun getNoticeInfo() = fire(Dispatchers.IO) {
        val response = WaSaiNetwork.getNoticeInfo()
        if (response.status == "success") {
            Result.success(response.data)
        } else {
            Result.failure(RuntimeException("获取失败"))
        }
    }

    fun getDonateList() = fire(Dispatchers.IO) {
        val response = WaSaiNetwork.getDonateList()
        if (response.status == "success") {
            Result.success(response.data)
        } else {
            Result.failure(RuntimeException("获取失败"))
        }
    }

    fun postHtml(school: String, type: String, html: String, qq: String) = fire(Dispatchers.IO) {
        Log.d("hhhhh", "Repository postHtml:")
        val response = WaSaiNetwork.postHtml(school, type, html, qq)
        Result.success(response)
    }

    fun getApplySchool() = fire(Dispatchers.IO) {
        val response = WaSaiNetwork.getApplySchool()
        if (response.status == "success") {
            Result.success(response.data)
        } else {
            Result.failure(RuntimeException("获取失败"))
        }
    }

    fun getStudentScore(xh: String, xn: String, xq: String) = fire(Dispatchers.IO) {
        var offset = 1
        var needExit = false
        val scoreList = ArrayList<StudentScoreResponse.ScoreItem>()

        while (!needExit) {
            val response = WaSaiNetwork.getStudentScore(xh, xn, xq, offset.toString())
            if (response.list.isNotEmpty()) {
                scoreList.addAll(response.list).also { offset++ }
                needExit = (response.totalPages == response.curPage)
            } else {
                break
            }
        }
        if (needExit) {
            Result.success(scoreList)
        } else {
            Result.failure(RuntimeException("操作失败"))
        }
    }

    fun getYktRecordLiveData(xgh: String, offset: String) = fire(Dispatchers.IO) {
        val response = WaSaiNetwork.getYktRecord(xgh, offset)
        if (response.list.isNotEmpty()) {
            Result.success(response)
        } else {
            Result.failure(RuntimeException("列表为空"))
        }

    }

    fun getTeachersLiveData(query: String) = fire(Dispatchers.IO) {
        val accessToken = getAccessToken()
        if (accessToken.isNotEmpty()) {
            val teachersResponse = WaSaiNetwork.getTeachers(query, accessToken)
            if (teachersResponse.data.teachers.isNotEmpty()) {
                Result.success(teachersResponse.data.teachers)
            } else {
                Result.failure(RuntimeException("操作失败"))
            }
        } else {
            Result.failure(RuntimeException("access_token is null"))
        }
    }

    private suspend fun getAccessToken(username: String = "201701010101"): String {
        val accessToken = WaSaiNetwork.getAccessToken(username)
        return accessToken.data.accessToken
    }

    fun importStudentSchedule(context: Context, xh: String, xn: String, xq: String, tableId: Int, newFlag: Boolean) = fire(Dispatchers.IO) {
        var offset = 1
        var needExit = false
        val courseList = ArrayList<StudentScheduleResponse.StudentCourseItem>()

        while (!needExit) {
            val response = WaSaiNetwork.getStudentSchedule(xh, xn, xq, offset.toString())
            if (response.list.isNotEmpty()) {
                tableName = response.list[0].xm
                courseList.addAll(response.list).also { offset++ }
                needExit = response.totalPages == response.curPage
            } else {
                break
            }
        }

        if (needExit) {
            val result = saveWeikeSchedule(context, tableId, newFlag, courseList)
            Result.success(result)
        } else {
            Result.failure(RuntimeException("操作失败"))
        }
    }

    fun importTeacherSchedule(context: Context, gh: String, xn: String, xq: String, tableId: Int, newFlag: Boolean) = fire(Dispatchers.IO) {

        var offset = 1
        var needExit = false
        val courseList = ArrayList<TeacherScheduleResponse.TeacherCourseItem>()

        while (!needExit) {
            val response = WaSaiNetwork.getTeacherSchedule(gh, xn, xq, offset.toString())
            if (response.list.isNotEmpty()) {
                tableName = response.list[0].jsxm
                courseList.addAll(response.list).also { offset++ }
                needExit = response.totalPages == response.curPage
            } else {
                break
            }
        }

        if (needExit) {
            val result = saveWeikeSchedule(context, tableId, newFlag, courseList)
            Result.success(result)
        } else {
            Result.failure(RuntimeException("操作失败"))
        }

    }


    private suspend fun <T> saveWeikeSchedule(context: Context, tableId: Int, newFlag: Boolean, responseList: T): Int {
        val weikeParser = WfustParser(responseList)
        return weikeParser.saveCourse(context, tableId) { baseList, detailList ->
            if (!newFlag) {
                courseDao.coverImport(baseList, detailList)
            } else {
                tableDao.insertTable(TableBean(id = tableId, tableName = tableName))
                courseDao.insertCourses(baseList, detailList)
            }
        }
    }

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) = liveData(context) {
        val result = try {
            block()
        } catch (e: Exception) {
            Result.failure<T>(e)
        }
        emit(result)
    }
}