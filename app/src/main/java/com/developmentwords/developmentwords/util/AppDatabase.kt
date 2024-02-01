package com.developmentwords.developmentwords.util

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@TypeConverters(
    TypeConverter::class
)
@Database(entities = [User::class, CourseItem::class, MyVoca::class], version = 17)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDAO
    abstract fun courseItemDao(): CourseItemDao
    abstract fun myVocaDao(): MyVocaDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app database")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return INSTANCE
        }
    }
}