package com.suqir.wasaischedule.logic.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.suqir.wasaischedule.logic.bean.*
import com.suqir.wasaischedule.logic.dao.*

@Database(entities = [CourseBaseBean::class, CourseDetailBean::class, AppWidgetBean::class, TimeDetailBean::class,
    TimeTableBean::class, TableBean::class],
        version = 1, exportSchema = false)

abstract class AppDatabase : RoomDatabase() {

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                                AppDatabase::class.java, "wasai")
//                                .allowMainThreadQueries()
                                .build()
                    }
                }
            }
            return INSTANCE!!
        }
    }

    abstract fun courseDao(): CourseDao

    abstract fun appWidgetDao(): AppWidgetDao

    abstract fun timeTableDao(): TimeTableDao

    abstract fun timeDetailDao(): TimeDetailDao

    abstract fun tableDao(): TableDao
}