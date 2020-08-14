package com.suqir.wasaischedule.ui.schedule_import.parser

import com.suqir.wasaischedule.logic.model.StudentScheduleResponse
import com.suqir.wasaischedule.logic.model.TeacherScheduleResponse
import com.suqir.wasaischedule.ui.schedule_import.bean.Course

/**
 * Author: Suqir
 * Date: 2020/8/12 10:14
 * Desc: 适配潍坊科技学院（已知问题：接口数据与真实情况有出入）
 **/
class WfustParser<T>(responseList: T) : Parser("", responseList) {
    override fun generateCourseList(): List<Course> {
        responseList as List<*>
        val courses = ArrayList<Course>()
        for (item in responseList) {
            when (item) {
                is TeacherScheduleResponse.TeacherCourseItem -> {
                    // 周次
                    val zc = item.zc.split(",")
                    for (i in zc.indices) {
                        val name = item.kcmc
                        val day = when (item.sksj.substring(0, 3)) {
                            "星期一" -> 1
                            "星期二" -> 2
                            "星期三" -> 3
                            "星期四" -> 4
                            "星期五" -> 5
                            "星期六" -> 6
                            else -> 7
                        }
                        val room = item.skdd
                        val teacher = item.jsxm
                        val startNode = (item.sksj.substring(4, 6)).toInt()
                        val endNode = (item.sksj.substring(item.sksj.lastIndex - 2, item.sksj.lastIndex)).toInt()
                        val startWeek = (zc[i].split("-")[0]).toInt()
                        val endWeek = (zc[i].split("-")[1]).toInt()
                        val type = item.dsz.toInt()
                        val course = Course(
                                name = name,
                                day = day,
                                room = room,
                                teacher = teacher,
                                startNode = startNode,
                                endNode = endNode,
                                startWeek = startWeek,
                                endWeek = endWeek,
                                type = type
                        )
                        courses.add(course)
                    }
                }
                is StudentScheduleResponse.StudentCourseItem -> {
                    // 周次
                    val zc = item.zc.split(",")
                    for (i in zc.indices) {
                        val name = item.kcmc
                        val day = when (item.sksj.substring(0, 3)) {
                            "星期一" -> 1
                            "星期二" -> 2
                            "星期三" -> 3
                            "星期四" -> 4
                            "星期五" -> 5
                            "星期六" -> 6
                            else -> 7
                        }
                        val room = item.skdd
                        val teacher = item.jsxm
                        val startNode = (item.sksj.substring(4, 6)).toInt()
                        val endNode = (item.sksj.substring(item.sksj.lastIndex - 2, item.sksj.lastIndex)).toInt()
                        val startWeek = (zc[i].split("-")[0]).toInt()
                        val endWeek = (zc[i].split("-")[1]).toInt()
                        val type = item.dsz.toInt()
                        val course = Course(
                                name = name,
                                day = day,
                                room = room,
                                teacher = teacher,
                                startNode = startNode,
                                endNode = endNode,
                                startWeek = startWeek,
                                endWeek = endWeek,
                                type = type
                        )
                        courses.add(course)
                    }
                }
                else -> courses.clear()
            }

        }
        return courses
    }
}