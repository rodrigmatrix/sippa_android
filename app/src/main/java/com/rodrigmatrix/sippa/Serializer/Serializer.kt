package com.rodrigmatrix.sippa.Serializer

import org.jsoup.Jsoup
import com.rodrigmatrix.sippa.persistance.StudentsDatabase


class Serializer {

    fun getStudentName(){

    }
    fun parseClasses(response: String): Class{
        var classes = arrayListOf<Class>()
        var size = 1
        var count = 1
        var grades = Grades("", "", "", "", "", "")
        var studentClass = Class("", "", "", "", 0, "", "",
            grades, emptyList(), "",  0, 0)
        Jsoup.parse(response).run {
            var tag = select("a[href]")
            for (it in tag) {
                if(it.attr("href").contains("id=")){
                    //println(it)
                    when (count) {
                        1 -> {
                            var arr = it.attr("href").split("id=")
                            studentClass.id = arr[1]
                            studentClass.code = it.text()
                        }
                        2 -> {
                            studentClass.name = it.text()
                        }
                        3 -> {
                            studentClass.professor = it.text()
                        }
                        4 -> {
                            studentClass.period = it.text()
                        }
                        5 -> {
                            studentClass.percentageAttendance = it.text()
                        }
                    }
                    count++
                    if(count == 5){
                        //println(studentClass)
                        count = 0
                        size++
                        classes.add(studentClass)
                        println(classes)
                    }
                }
            }
        }
        return studentClass
    }
    fun parseGrades(response: String){
        //Precisa usar api.setClass para não dar erro
    }
    fun parseHorasComplementares(response: String, database: StudentsDatabase){
        var arr = response.split("Total de Horas em Atividades Complementares: ")
        arr = arr[1].split("</h2>")
        //println("horas: " + arr[0])
        var student = database.StudentDao().getStudent()
        student.horasComplementares = arr[1]
        database.StudentDao().insert(student)
        println(database.StudentDao().getStudent().horasComplementares)
    }
    fun parseFiles(response: String){
        //Precisa usar api.setClass para não dar erro
    }
}