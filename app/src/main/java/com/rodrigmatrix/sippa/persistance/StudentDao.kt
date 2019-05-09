package com.rodrigmatrix.sippa.persistance

import androidx.room.*


@Dao
interface StudentDao {

    @Query("SELECT * FROM students")
    fun getStudent(): List<Student>

    @Query("SELECT * FROM students where id = 0")
    fun getJsession(): Student

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(student: Student)

    @Query("DELETE FROM students")
    fun delete()


}