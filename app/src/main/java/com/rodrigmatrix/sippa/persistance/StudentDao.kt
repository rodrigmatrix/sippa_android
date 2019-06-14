package com.rodrigmatrix.sippa.persistance

import androidx.room.*


@Dao
interface StudentDao {

    @Query("SELECT * FROM students where id = 0")
    fun getStudent(): Student

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(student: Student)

    @Query("DELETE FROM students")
    fun deleteStudent()



    @Query("SELECT * FROM classes")
    fun getClasses(): List<Class>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertClass(studentClass: Class)

    @Query("DELETE FROM classes")
    fun deleteClasses()



    @Query("SELECT * FROM news WHERE classId = id")
    fun getNews(id: Int): List<News>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNews(news: News)

    @Query("DELETE FROM news")
    fun deleteNews()

    @Query("DELETE FROM news WHERE classId = id")
    fun deleteNewsFromClass(id: String)



    @Query("SELECT * FROM grades WHERE classId = id")
    fun getGrades(id: Int): List<Grade>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGrade(grade: Grade)

    @Query("DELETE FROM grades")
    fun deleteGrades()

    @Query("DELETE FROM grades WHERE classId = id")
    fun deleteGradesFromClass(id: String)



    @Query("SELECT * FROM classPlan WHERE classId = id")
    fun getClassPlan(id: Int): List<ClassPlan>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertClassPlan(classPlan: ClassPlan)

    @Query("DELETE FROM classPlan")
    fun deleteClassPlan()

    @Query("DELETE FROM classPlan WHERE classId = id")
    fun deleteClassPlanFromClass(id: String)



    @Query("SELECT * FROM files WHERE classId = id")
    fun getFiles(id: String): List<File>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFile(file: File)

    @Query("DELETE FROM files")
    fun deleteFiles()

    @Query("DELETE FROM files WHERE classId = id")
    fun deleteFilesFromClass(id: String)


}