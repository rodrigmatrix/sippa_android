package com.rodrigmatrix.sippa.serializer

import com.rodrigmatrix.sippa.persistance.*
import org.jsoup.Jsoup
import java.lang.IndexOutOfBoundsException
import java.lang.Math.random

class Serializer {

    fun parseClasses(response: String): MutableList<Class>{
        val classes: MutableList<Class> = mutableListOf()
        var size = 1
        var count = 0
        val studentClass = Class("", "", "", "", "", 0, 0, 0)
        Jsoup.parse(response).run {
            val tag = select("a[href]")
            tag.forEach{
                if(it.attr("href").contains("id=")){
                    count++
                    when (count) {
                        1 -> {
                            val arr = it.attr("href").split("id=")
                            when {
                                arr.size >= 2 -> studentClass.id = arr[1]
                                else -> studentClass.id = "1"
                            }
                        }
                        2 -> {
                            studentClass.name = it.text()
                        }
                        3 -> {
                            studentClass.professorName = it.text()
                        }
                        5 -> {
                            studentClass.percentageAttendance = it.text()
                            count = 0
                            size++
                            classes.add(Class(studentClass.id, studentClass.name, studentClass.professorName, studentClass.professorEmail,
                                studentClass.percentageAttendance, 0, studentClass.missed, 1))
                        }
                    }
                }
            }
        }
        return classes
    }

    fun parseAttendance(response: String): Attendance{
        val attendance = response.split("de Frequência; ",  " Presenças em Horas;")
        val missed = response.split("Presenças em Horas;  ",  " Faltas em Horas")
        return if(attendance.size >= 2 && missed.size >= 2){
            Attendance(attendance[1].toInt(), missed[1].toInt())
        } else{
            Attendance(0,0)
        }
    }

    fun parseClassPlan(classId: String, response: String): MutableList<ClassPlan>{
        val res = response.replace("<table>", "")
        val classesPlan = mutableListOf<ClassPlan>()
        val classPlan = ClassPlan(random().toInt(), "", "", "", "", "")
        val document = Jsoup.parse(res)
        val tbody = document.getElementsByTag("tbody")
        val plan = tbody.select("td")
        var count = 1
        plan.forEach{
           when(count){
               1 -> {
                   classPlan.date = "Nº " + it.text()
               }
               2 -> {
                   val arr = it.text()
                   var date = "Data não cadastrada"
                   var content = "Plano não cadastrado"
                   if(arr.isNotEmpty() && arr.length >= 10){
                       date = arr.replaceRange(10, arr.length, "")
                       content = arr.replaceRange(0,10, "")
                   }
                   classPlan.date = date
                   classPlan.planned = content
               }
               3 -> {
                   val arr = it.text()
                   when {
                       arr != "" -> {
                           var content = ""
                           if(arr.isNotEmpty() && arr.length >= 10){
                               content = arr.replaceRange(0,10, "")
                           }
                           when(content){
                               "" -> classPlan.diary = "Não cadastrado"
                               else -> classPlan.diary = content
                           }
                       }
                       else -> classPlan.diary = "Aula ainda não foi apresentada"
                   }
               }
               4 -> {
                   when {
                       it.text() == "" -> classPlan.attendance = "Frequência não cadastrada"
                       it.text().toInt() > 0 -> classPlan.attendance = "Presente: " + "2" + " horas"
                       else -> classPlan.attendance = "Falta: 2 horas"
                   }
                   classesPlan.add(ClassPlan(random().toInt(), classId, classPlan.date, classPlan.planned, classPlan.attendance, classPlan.diary))
                   count = 0
               }
           }
            count++
        }
        if(classesPlan.size == 0){
            classesPlan.add(ClassPlan(random().toInt(), classId,"Plano não criado","Este professor não cadastrou nenhum plano de aula nessa disciplina", "", ""))
        }
        return classesPlan
    }

    fun parseProfessorEmail(response: String): String{
        Jsoup.parse(response).run {
            val email = getElementsByTag("h2")
            return try {
                val arr = email[0]
                val split = arr.text().split("- ", "<")
                if(split.isNotEmpty() && split.size >= 2){
                    split[1]
                } else{
                    "Email não cadastrado"
                }
            }catch(e: IndexOutOfBoundsException){
                "Email não encontrado"
            }
        }
    }

    fun parseGrades(classId: String, response: String): MutableList<Grade>{
        //Precisa usar api.setClass para não dar erro
        val gradesList: MutableList<Grade> = mutableListOf()
        Jsoup.parse(response).run {
            val thead = getElementsByTag("thead")
            val names = thead.select("th")
            val tbody = getElementsByTag("tbody")
            val grades = tbody.select("td")
            var index = 2
            names.forEach{
                if(it.text() != "Aluno"){
                    val grade = Grade(random().toInt(), classId, it.text(), grades[index].text())
                    gradesList.add(grade)
                    index++
                }
            }
        }
        return gradesList
    }

    fun parseNews(classId: String, response: String): MutableList<News>{
        //Precisa usar api.setClass para não dar erro
        val newsList: MutableList<News> = mutableListOf()
        Jsoup.parse(response).run {
            val coluna0 = getElementsByClass("tabela-coluna0")
            val coluna1 = getElementsByClass("tabela-coluna1")
            for ((index, date) in coluna0.withIndex()) {
                val news = News(random().toInt(), classId, date.text(), coluna1[index].text())
                newsList.add(news)
            }
        }
        return newsList
    }

    fun parseHorasComplementares(response: String): MutableList<HoraComplementar>{
        val horas = mutableListOf<HoraComplementar>()
        var id = 0
        Jsoup.parse(response).run {
            val body = getElementsByTag("td")
            val horaDef = HoraComplementar(random().toInt(), "", "", "")
            var index = 1
            body.forEach{
                when(index){
                    1 -> {
                        horaDef.name = it.text()
                    }
                    3 -> {
                        horaDef.professor = it.text()
                    }
                    4 -> {
                        horaDef.total = """Horas ganhas: ${it.text()}"""
                        horas.add(HoraComplementar(id, horaDef.name, "Professor: " + horaDef.professor, horaDef.total))
                        index = 0
                    }
                }
                id++
                index++
            }
        }
        var arr = response.split("Total de Horas em Atividades Complementares: ")
        arr = arr[1].split("</h2>")
        horas.add(HoraComplementar(id,"Total de Horas Complementares", " ", "Total: "+arr[0]))
        return horas
    }

    fun parseFiles(classId: String,response: String): MutableList<File>{
        //Precisa usar api.setClass para não dar erro
        val filesList: MutableList<File> = mutableListOf()
        var id = 0
        Jsoup.parse(response).run {
            val files = select("a[href]")
            files.forEach{
                if(it.attr("href").contains("id=")){
                    val arr = it.attr("href").split("id=")
                    filesList.add(File(random().toInt(), classId, arr[1]))
                }
                id++
            }
        }
        if(filesList.size == 0){
            filesList.add(File(random().toInt(),classId,"Nenhum arquivo disponível nessa disciplina"))
        }
        return filesList
    }
}