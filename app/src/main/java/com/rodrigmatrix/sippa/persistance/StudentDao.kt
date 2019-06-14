package com.rodrigmatrix.sippa.persistance

import androidx.room.*


@Dao
interface StudentDao {

    @Query("SELECT * FROM students where id = 0")
    fun getStudent(): Student

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStudent(student: Student)

    @Query("DELETE FROM students")
    fun deleteStudent()



    @Query("SELECT * FROM classes")
    fun getClasses(): MutableList<Class>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertClass(studentClass: Class)

    @Query("DELETE FROM classes")
    fun deleteClasses()



    @Query("SELECT * FROM news WHERE classId LIKE :id")
    fun getNews(id: String): List<News>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNews(news: News)

    @Query("DELETE FROM news")
    fun deleteNews()

    @Query("DELETE FROM news WHERE classId LIKE :id")
    fun deleteNewsFromClass(id: String)



    @Query("SELECT * FROM grades WHERE classId LIKE :id")
    fun getGrades(id: String): List<Grade>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGrade(grade: Grade)

    @Query("DELETE FROM grades")
    fun deleteGrades()

    @Query("DELETE FROM grades WHERE classId LIKE :id")
    fun deleteGradesFromClass(id: String)



    @Query("SELECT * FROM classPlan WHERE classId LIKE :id")
    fun getClassPlan(id: Int): List<ClassPlan>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertClassPlan(classPlan: ClassPlan)

    @Query("DELETE FROM classPlan")
    fun deleteClassPlan()

    @Query("DELETE FROM classPlan WHERE classId LIKE :id")
    fun deleteClassPlanFromClass(id: String)



    @Query("SELECT * FROM files WHERE classId LIKE :id")
    fun getFiles(id: String): List<File>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFile(file: File)

    @Query("DELETE FROM files")
    fun deleteFiles()

    @Query("DELETE FROM files WHERE classId LIKE :id")
    fun deleteFilesFromClass(id: String)
}