package com.rodrigmatrix.sippa.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rodrigmatrix.sippa.persistance.*

@Database(entities = [Student::class, Class::class, News::class, Grade::class, ClassPlan::class, File::class, HoraComplementar::class], version = 19, exportSchema = false)
abstract class StudentsDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao

    companion object {

        @Volatile private var instance: StudentsDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it}
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                StudentsDatabase::class.java, "database.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
    }
}