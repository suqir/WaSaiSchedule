package com.suqir.wasaischedule.ui.schedule_import.parser.qz

class QzCrazyParser(source: String) : QzParser(source) {
    override val tableName: String
        get() = "kbcontent1"
}