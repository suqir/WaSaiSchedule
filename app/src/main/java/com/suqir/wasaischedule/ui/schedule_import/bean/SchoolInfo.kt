package com.suqir.wasaischedule.ui.schedule_import.bean

data class SchoolInfo(
        var sortKey: String,
        val name: String,
        val url: String = "",
        val type: String?)