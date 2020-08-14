package com.suqir.wasaischedule.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.suqir.wasaischedule.logic.bean.TableBean
import com.suqir.wasaischedule.logic.bean.TimeDetailBean
import com.suqir.wasaischedule.logic.bean.TimeTableBean
import com.suqir.wasaischedule.logic.database.AppDatabase
import com.suqir.wasaischedule.utils.CourseUtils

class TimeSettingsViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var table: TableBean

    private val dataBase = AppDatabase.getDatabase(application)
    private val timeDao = dataBase.timeDetailDao()
    private val timeTableDao = dataBase.timeTableDao()
    val timeTableList = arrayListOf<TimeTableBean>()
    val timeList = arrayListOf<TimeDetailBean>()
    val timeSelectList = arrayListOf<String>()

    var entryPosition = 0
    var selectedId = 1

    suspend fun addNewTimeTable(name: String) {
        timeTableDao.initTimeTable(TimeTableBean(id = 0, name = name))
    }

    suspend fun initTimeTableData(id: Int) {
        val timeList = listOf(
                TimeDetailBean(1, "08:00", "08:45", 1),
                TimeDetailBean(2, "08:55", "09:40", 1),
                TimeDetailBean(3, "10:00", "10:45", 1),
                TimeDetailBean(4, "10:55", "11:40", 1),
                TimeDetailBean(5, "14:00", "14:45", 1),
                TimeDetailBean(6, "14:55", "15:40", 1),
                TimeDetailBean(7, "16:00", "16:45", 1),
                TimeDetailBean(8, "16:55", "17:40", 1),
                TimeDetailBean(9, "18:50", "19:35", 1),
                TimeDetailBean(10, "19:45", "20:30", 1),
                TimeDetailBean(11, "00:00", "00:00", 1),
                TimeDetailBean(12, "00:00", "00:00", 1),
                TimeDetailBean(13, "00:00", "00:00", 1),
                TimeDetailBean(14, "00:00", "00:00", 1),
                TimeDetailBean(15, "00:00", "00:00", 1),
                TimeDetailBean(16, "00:00", "00:00", 1),
                TimeDetailBean(17, "00:00", "00:00", 1),
                TimeDetailBean(18, "00:00", "00:00", 1),
                TimeDetailBean(19, "00:00", "00:00", 1),
                TimeDetailBean(20, "00:00", "00:00", 1),
                TimeDetailBean(21, "00:00", "00:00", 1),
                TimeDetailBean(22, "00:00", "00:00", 1),
                TimeDetailBean(23, "00:00", "00:00", 1),
                TimeDetailBean(24, "00:00", "00:00", 1),
                TimeDetailBean(25, "00:00", "00:00", 1),
                TimeDetailBean(26, "00:00", "00:00", 1),
                TimeDetailBean(27, "00:00", "00:00", 1),
                TimeDetailBean(28, "00:00", "00:00", 1),
                TimeDetailBean(29, "00:00", "00:00", 1),
                TimeDetailBean(30, "00:00", "00:00", 1)
        )
        timeDao.insertTimeList(timeList)
    }

    suspend fun deleteTimeTable(timeTableBean: TimeTableBean) {
        timeTableDao.deleteTimeTable(timeTableBean)
    }

    fun getTimeTableList(): LiveData<List<TimeTableBean>> {
        return timeTableDao.getTimeTableList()
    }

    fun getTimeData(id: Int): LiveData<List<TimeDetailBean>> {
        return timeDao.getTimeListLiveData(id)
    }

    suspend fun saveDetailData(tablePosition: Int) {
        timeTableDao.updateTimeTable(timeTableList[tablePosition])
        timeDao.updateTimeDetailList(timeList)
    }

    fun initTimeSelectList() {
        for (i in 6..23) {
            for (j in 0..55 step 5) {
                val h = if (i < 10) "0$i" else i.toString()
                val m = if (j < 10) "0$j" else j.toString()
                timeSelectList.add("$h:$m")
            }
        }
    }

    fun refreshEndTime(min: Int) {
        timeList.forEach {
            it.endTime = CourseUtils.calAfterTime(it.startTime, min)
        }
    }
}