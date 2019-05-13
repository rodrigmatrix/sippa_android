package com.rodrigmatrix.sippa.Serializer

import org.jsoup.Jsoup
import com.rodrigmatrix.sippa.persistance.StudentsDatabase


class Serializer {

    fun parseClasses(response: String): MutableList<Class>{
        var classes: MutableList<Class> = mutableListOf()
        var size = 1
        var count = 0
        var grades: MutableList<Grade> = mutableListOf()
        var newsList: MutableList<News> = mutableListOf()
        var classPlan: MutableList<ClassPlan> = mutableListOf()
        var studentClass = Class("", "", "", "", 0, "", "",
            grades, newsList, classPlan,"",  0, 0)
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
                            studentClass.percentageAttendance = it.text()
                            //println(studentClass)
                            count = 0
                            size++
                            classes.add(studentClass)
                            //println(classes)
                        }
                    }
                }
            }
        }
        return classes
    }

    fun parseAttendance(response: String){

    }
    fun parseClassPlan(response: String){

    }
    fun parseGrades(response: String, database: StudentsDatabase): MutableList<Grade>{
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
        //println(gradesList)
        return gradesList
    }
    fun parseNews(response: String): MutableList<News>{
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