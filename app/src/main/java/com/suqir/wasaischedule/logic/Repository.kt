package com.suqir.wasaischedule.logic

import android.content.Context
import androidx.lifecycle.liveData
import com.suqir.wasaischedule.App.Companion.context
import com.suqir.wasaischedule.logic.bean.TableBean
import com.suqir.wasaischedule.logic.database.AppDatabase
import com.suqir.wasaischedule.logic.model.StudentScheduleResponse
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

    fun getUpdateInfo() = fire(Dispatchers.IO) {
        val updateResponse = WaSaiNetwork.getUpdateInfo()
        Result.success(updateResponse)
    }

    fun getTeachers(searchName: String) = fire(Dispatchers.IO) {
        val accessResponse = WaSaiNetwork.getAccessToken("201701010101")
        if (accessResponse.msg == "操作成功") {
            val accessToken = accessResponse.data.accessToken
            val response = WaSaiNetwork.getTeachers(searchName, accessToken)
            if (response.msg == "操作成功") {
                Result.success(response.data.teachers)
            } else {
                Result.failure(RuntimeException("操作失败"))
            }
        } else {
            Result.failure(RuntimeException("操作失败"))
        }
    }

    fun importStudentSchedule(context: Context, xh: String, xn: String, xq: String, tableId: Int, newFlag: Boolean) = fire(Dispatchers.IO) {
        var offset = 1
        val responseList = ArrayList<StudentScheduleResponse.StudentCourseItem>()
        var response = WaSaiNetwork.getStudentSchedule(xh, xn, xq, offset.toString())
        if (response.msg == "操作成功") {
            tableName = response.list[0].xm
            responseList.addAll(response.list).also { offset++ }
            for (page in offset..response.totalPage) {
                response = WaSaiNetwork.getStudentSchedule(xh, xn, xq, offset.toString())
                responseList.addAll(response.list)
            }
            val result = saveWeikeSchedule(context, tableId, newFlag, responseList)
            Result.success(result)
        } else {
            Result.failure(RuntimeException("操作失败"))
        }
    }

    fun importTeacherSchedule(context: Context, gh: String, xn: String, xq: String, tableId: Int, newFlag: Boolean) = fire(Dispatchers.IO) {
        var offset = 1
        val responseList = ArrayList<TeacherScheduleResponse.TeacherCourseItem>()
        var response = WaSaiNetwork.getTeacherSchedule(gh, xn, xq, offset.toString())
        if (response.msg == "操作成功") {
            tableName = response.list[0].jsxm
            responseList.addAll(response.list).also { offset++ }
            for (page in offset..response.totalPage) {
                response = WaSaiNetwork.getTeacherSchedule(gh, xn, xq, offset.toString())
                responseList.addAll(response.list)
            }
            val result = saveWeikeSchedule(context, tableId, newFlag, responseList)
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