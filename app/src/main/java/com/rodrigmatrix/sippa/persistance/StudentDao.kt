package com.rodrigmatrix.sippa.persistance

import androidx.room.*


@Dao
interface StudentDao {

    @Query("SELECT * FROM students")
    fun getStudents(): List<Student>

    @Query("SELECT * FROM students where id = 0")
    fun getStudent(): Student

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(student: Student)

    @Query("DELETE FROM students")
    fun delete()


}