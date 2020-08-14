package com.suqir.wasaischedule.ui.schedule_appwidget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.suqir.wasaischedule.logic.bean.AppWidgetBean
import com.suqir.wasaischedule.logic.bean.TableBean
import com.suqir.wasaischedule.logic.bean.TableSelectBean
import com.suqir.wasaischedule.logic.database.AppDatabase

class WeekScheduleAppWidgetConfigViewModel(application: Application) : AndroidViewModel(application) {
    private val dataBase = AppDatabase.getDatabase(application)
    private val tableDao = dataBase.tableDao()
    private val widgetDao = dataBase.appWidgetDao()

    suspend fun getDefaultTable(): TableBean {
        return tableDao.getDefaultTable()
    }

    suspend fun getTableById(id: Int): TableBean? {
        return tableDao.getTableById(id)
    }

    suspend fun insertWeekAppWidgetData(appWidget: AppWidgetBean) {
        widgetDao.insertAppWidget(appWidget)
    }

    suspend fun getTableList(): List<TableSelectBean> {
        return tableDao.getTableSelectList()
    }
}