package com.suqir.wasaischedule.ui.schedule_import.parser.qz

class QzBrParser(source: String) : QzParser(source) {

    override fun parseCourseName(infoStr: String): String {
        return infoStr.substringBefore("<br>").trim()
    }

}