package com.suqir.wasaischedule.ui.schedule_import.parser

import android.content.Context
import com.suqir.wasaischedule.logic.bean.CourseBaseBean
import com.suqir.wasaischedule.logic.bean.CourseDetailBean
import com.suqir.wasaischedule.ui.schedule_import.Common
import com.suqir.wasaischedule.ui.schedule_import.bean.Course
import com.suqir.wasaischedule.utils.ViewUtils

abstract class Parser(val source: String, val responseList: Any? = null) {

    private val _baseList: ArrayList<CourseBaseBean> = arrayListOf()
    private val _detailList: ArrayList<CourseDetailBean> = arrayListOf()

    abstract fun generateCourseList(): List<Course>

    private fun convertCourse(context: Context, tableId: Int) {
        generateCourseList().forEach { course ->
            var id = Common.findExistedCourseId(_baseList, course.name)
            if (id == -1) {
                id = _baseList.size
                _baseList.add(
                        CourseBaseBean(
                                id = id, courseName = course.name,
                                color = "#${Integer.toHexString(ViewUtils.getCustomizedColor(context, id % 9))}",
                                tableId = tableId
                        )
                )
            }
            var step = course.endNode - course.startNode + 1
            if (step < 1) step = 1
            _detailList.add(CourseDetailBean(
                    id = id, room = course.room,
                    teacher = course.teacher,
                    day = if (course.day < 1) 1 else course.day,
                    step = step,
                    startWeek = if (course.startWeek < 1) 1 else course.startWeek,
                    endWeek = if (course.endWeek < 1) 1 else course.endWeek,
                    type = course.type,
                    startNode = if (course.startNode < 1) 1 else course.startNode,
                    tableId = tableId
            ))
        }
    }

    suspend fun saveCourse(context: Context, tableId: Int, block: suspend (baseList: List<CourseBaseBean>,
                                                                           detailList: List<CourseDetailBean>) -> Unit): Int {
        convertCourse(context, tableId)
        if (_baseList.isEmpty()) throw Exception("导入数据为空>_<请确保选择正确的教务类型\n以及到达显示课程的页面")
        block(_baseList, _detailList)
        return _baseList.size
    }

}