package com.suqir.wasaischedule.ui.schedule_import.parser

import com.suqir.wasaischedule.ui.schedule_import.bean.Course
import org.jsoup.Jsoup

/**
 * Author: Suqir
 * Date: 2020/9/10 14:52
 * Desc: 新版青果软件教务系统
 **/
class NewQingGuoParser(source: String) : Parser(source) {

    override fun generateCourseList(): List<Course> {
        val courseList = arrayListOf<Course>()
        val xml = source.substringAfter("</html>")
        val doc = Jsoup.parse(xml)
        val table = doc.getElementById("mytable")
        val trs = table.getElementsByTag("tr").subList(1, 6)

        var courseName: String
        var teacher: String
        var room: String
        var startWeek: Int
        var endWeek: Int
        var startNode: Int
        var endNode: Int
        var type: Int

        for (i in trs.indices) {
            val tds = trs[i].getElementsByClass("td")
            if (tds.isEmpty()) continue
            for (j in tds.indices) {
                val str = tds[j].getElementsByTag("div").text()
                if (str.isNullOrEmpty()) continue
                val data = str.split(" ")
                val dataLength = data.size
                if (dataLength % 6 == 0) {
                    for (index in 0..(dataLength / 2) step 6) {
                        courseName = data[index]
                        teacher = data[index + 1]
                        room = data[index + 5]
                        type = if (data[index + 3] == "单") 1 else 2
                        val weeks = data[index + 2].split("-")
                        startWeek = weeks[0].toInt()
                        endWeek = weeks[1].toInt()
                        val nodes = data[index + 4].replace("[", "").replace("]", "").split("-")
                        startNode = nodes[0].toInt()
                        endNode = nodes[1].toInt()
                        courseList.add(
                                Course(
                                        name = courseName, day = j + 1, room = room, teacher = teacher, startNode = startNode,
                                        endNode = endNode, startWeek = startWeek, endWeek = endWeek, type = type
                                )
                        )
                    }
                } else {
                    courseName = data[0]
                    teacher = data[1]
                    room = data[data.size - 1]
                    type = 0
                    val weekAndNode = data[2].replace("[", "-").replace("]", "").split("-")
                    startWeek = weekAndNode[0].toInt()
                    endWeek = weekAndNode[1].toInt()
                    startNode = weekAndNode[2].toInt()
                    endNode = weekAndNode[3].toInt()
                    courseList.add(
                            Course(
                                    name = courseName, day = j + 1, room = room, teacher = teacher, startNode = startNode,
                                    endNode = endNode, startWeek = startWeek, endWeek = endWeek, type = type
                            )
                    )
                }
            }
        }

        return courseList
    }
}