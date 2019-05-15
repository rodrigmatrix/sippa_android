package com.rodrigmatrix.sippa.Serializer

import com.rodrigmatrix.sippa.Api
import org.jsoup.Jsoup
import com.rodrigmatrix.sippa.persistance.StudentsDatabase
import org.jetbrains.anko.doAsync


class Serializer {

    fun parseClasses(response: String, database: StudentsDatabase): MutableList<Class>{
        var classes: MutableList<Class> = mutableListOf()
        var size = 1
        var count = 0
        var grades: MutableList<Grade> = mutableListOf()
        var newsList: MutableList<News> = mutableListOf()
        var classPlan: MutableList<ClassPlan> = mutableListOf()
        var filesList: MutableList<File> = mutableListOf()
        var api = Api()
        var attendance = Attendance(0, 0)
        var studentClass = Class("", "", "", "", "", "",
            grades, newsList, classPlan, filesList, "", attendance)
        Jsoup.parse(response).run {
            var tag = select("a[href]")
            for (it in tag) {
                if(it.attr("href").contains("id=")){
                    //println(it)
                    count++
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
                            try {
                                studentClass.percentageAttendance = it.text()
                                //studentClass.news = parseNews(res)
                                //api.getGrades(database)
                                //Thread.sleep(5000)
                                //var grades = database.StudentDao().getStudent().responseHtml
                                //studentClass.grades = parseGrades(grades)
                                //println(studentClass)
                                count = 0
                                size++
                                classes.add(com.rodrigmatrix.sippa.Serializer.Class(studentClass.id, studentClass.name, studentClass.professor, studentClass.professorEmail, studentClass.period
                                    , studentClass.code, studentClass.grades, studentClass.news, studentClass.classPlan, studentClass.files, studentClass.percentageAttendance, studentClass.attendance))
//                                println(studentClass.id)
//                                println(studentClass.name)
//                                println(studentClass.professor)
//                                println(studentClass.grades)
//                                println(studentClass.news)
//                                println("porcentagem: " + studentClass.percentageAttendance)
//                                println(studentClass.attendance)
                            }
                            catch(e: Exception){
                                println(e)
                            }

                        }
                    }
                }
            }
        }
        return classes
    }

    fun parseAttendance(response: String): Attendance{
        var attendance = response.split("de Frequência; ",  " Presenças em Horas;")
        var missed = response.split("Presenças em Horas;  ",  " Faltas em Horas")
        println(attendance[1] + "  " + missed[1])
        return Attendance(attendance[1].toInt(), missed[1].toInt())
    }

    fun parseClassPlan(response: String){

    }

    fun parseGrades(response: String): MutableList<Grade>{
        //Precisa usar api.setClass para não dar erro
        var gradesList: MutableList<Grade> = mutableListOf()
        Jsoup.parse(response).run {
            var thead = getElementsByTag("thead")
            var names = thead.select("th")
            var tbody = getElementsByTag("tbody")
            var grades = tbody.select("td")
            var index = 2
            for(it in names){
                if(it.text() != "Aluno"){
                    var grade = Grade(it.text(), grades[index].text())
                    gradesList.add(grade)
                    index++
                }
            }
        }
        return gradesList
    }

    fun parseNews(response: String): MutableList<News>{
        //println("res news" + response)
        //Precisa usar api.setClass para não dar erro
        var newsList: MutableList<News> = mutableListOf()
        Jsoup.parse(response).run {
            var coluna0 = getElementsByClass("tabela-coluna0")
            var coluna1 = getElementsByClass("tabela-coluna1")
            var index = 0
            for (date in coluna0) {
                var news = News(date.text(), coluna1[index].text())
                newsList.add(news)
                index++
            }
        }
        //println(newsList)
        return newsList
    }

    fun parseHorasComplementares(response: String){
        var arr = response.split("Total de Horas em Atividades Complementares: ")
        arr = arr[1].split("</h2>")
        //println("horas: " + arr[0])
    }

    fun parseFiles(response: String): MutableList<File>{
        //Precisa usar api.setClass para não dar erro
        var filesList: MutableList<File> = mutableListOf()
        Jsoup.parse(response).run {
            var files = select("a[href]")
            for(it in files){
                if(it.attr("href").contains("id=")){
                    var arr = it.attr("href").split("id=")
                    var file = File(arr[1])
                    filesList.add(file)
                }
            }
        }
        return filesList
    }
}