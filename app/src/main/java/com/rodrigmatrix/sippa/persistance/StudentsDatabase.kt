package com.rodrigmatrix.sippa.persistance

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Student::class), version = 3)
abstract class StudentsDatabase : RoomDatabase() {

    abstract fun StudentDao(): StudentDao

    companion object {

        @Volatile private var instance: StudentsDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it}
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                StudentsDatabase::class.java, "student.db")
                .build()
    }
}