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
                val divs = tds[j].getElementsByTag("div")
                for (div in divs) {
                    val str = div.text()
                    if (str.isNullOrEmpty()) continue
                    val data = str.split(" ")
                    when (val dataLength = data.size) {
                        5 -> {
                            // dataLength = 5：没有上课地点，有单双周
                            courseName = data[0]
                            teacher = data[1]
                            room = ""
                            type = if (data[3] == "单") 1 else 2
                            val weeks = data[2].split("-")
                            startWeek = weeks[0].toInt()
                            endWeek = weeks[1].toInt()
                            val nodes = data[4].replace("[", "").replace("]", "").split("-")
                            startNode = nodes[0].toInt()
                            endNode = nodes[1].toInt()
                            courseList.add(
                                    Course(
                                            name = courseName, room = room, day = j + 1, teacher = teacher, startNode = startNode,
                                            endNode = endNode, startWeek = startWeek, endWeek = endWeek, type = type
                                    )
                            )
                        }
                        6 -> {
                            // dataLength = 6：有上课地点，有单双周
                            courseName = data[0]
                            teacher = data[1]
                            room = data[5]
                            type = if (data[3] == "单") 1 else 2
                            val weeks = data[2].split("-")
                            startWeek = weeks[0].toInt()
                            endWeek = weeks[1].toInt()
                            val nodes = data[4].replace("[", "").replace("]", "").split("-")
                            startNode = nodes[0].toInt()
                            endNode = nodes[1].toInt()
                            courseList.add(
                                    Course(
                                            name = courseName, room = room, day = j + 1, teacher = teacher, startNode = startNode,
                                            endNode = endNode, startWeek = startWeek, endWeek = endWeek, type = type
                                    )
                            )
                        }
                        else -> {
                            // dataLength = 4：有上课地点，但没单双周
                            // dataLength = 3：没有上课地点，没单双周
                            courseName = data[0]
                            teacher = data[1]
                            room = if (dataLength == 4) data[3] else ""
                            type = 0
                            val weekAndNode = data[2].replace("[", "-").replace("]", "").split("-")
                            startWeek = weekAndNode[0].toInt()
                            endWeek = weekAndNode[1].toInt()
                            startNode = weekAndNode[2].toInt()
                            endNode = weekAndNode[3].toInt()
                            courseList.add(
                                    Course(
                                            name = courseName, room = room, day = j + 1, teacher = teacher, startNode = startNode,
                                            endNode = endNode, startWeek = startWeek, endWeek = endWeek, type = type
                                    )
                            )
                        }
                    }
                }

            }
        }

        return courseList
    }
}