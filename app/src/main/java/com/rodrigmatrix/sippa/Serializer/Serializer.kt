package com.rodrigmatrix.sippa.serializer

import org.jsoup.Jsoup

class Serializer {

    fun parseClasses(response: String): MutableList<com.rodrigmatrix.sippa.persistance.Class>{
        var classes: MutableList<com.rodrigmatrix.sippa.persistance.Class> = mutableListOf()
        var size = 1
        var count = 0
        val grades = mutableListOf<Grade>()
        var newsList = mutableListOf<News>()
        var classPlan = mutableListOf<ClassPlan>()
        var filesList = mutableListOf<File>()
        var attendance = Attendance(0, 0)
        var studentClass = Class("", "", "", "", "", "",
            grades, newsList, classPlan, filesList, "", attendance, 1)
        Jsoup.parse(response).run {
            val tag = select("a[href]")
            for (it in tag) {
                if(it.attr("href").contains("id=")){
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
                            count = 0
                            size++
                            classes.add(com.rodrigmatrix.sippa.persistance.Class(studentClass.id, studentClass.name, studentClass.professor, studentClass.professorEmail,
                                studentClass.percentageAttendance, 0, studentClass.attendance.totalMissed, 1))
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
        return Attendance(attendance[1].toInt(), missed[1].toInt())
    }

    fun parseClassPlan(response: String): MutableList<ClassPlan>{
        val res = response.replace("<table>", "")
        var classesPlan = mutableListOf<ClassPlan>()
        var classPlan = ClassPlan("", "", "", "", "")
        val document = Jsoup.parse(res)
        var tbody = document.getElementsByTag("tbody")
        var plan = tbody.select("td")
        var count = 1
        for(it in plan){
           when(count){
               1 -> {
                   classPlan.classNumber = "Nº " + it.text()
               }
               2 -> {
                   var arr = it.text()
                   var date = "Data não cadastrada"
                   var content = "Plano não cadastrado"
                   if(arr.isNotEmpty() && arr.length >= 10){
                       date = arr.replaceRange(10, arr.length, "")
                       content = arr.replaceRange(0,10, "")
                   }
                   classPlan.classDate = date
                   classPlan.classPlanned = content
               }
               3 -> {
                   var arr = it.text()
                   when {
                       arr != "" -> {
                           var content = ""
                           if(arr.isNotEmpty() && arr.length >= 10){
                               content = arr.replaceRange(0,10, "")
                           }
                           when(content){
                               "" -> classPlan.classDiary = "Não cadastrado"
                               else -> classPlan.classDiary = content
                           }
                       }
                       else -> classPlan.classDiary = "Aula ainda não foi apresentada"
                   }
               }
               4 -> {
                   when {
                       it.text() == "" -> classPlan.attendance = "Frequência não cadastrada"
                       it.text().toInt() > 0 -> classPlan.attendance = "Presente: " + "2" + " horas"
                       else -> classPlan.attendance = "Falta: 2 horas"
                   }
                   classesPlan.add(ClassPlan(classPlan.classNumber, classPlan.classPlanned, classPlan.classDate, classPlan.classDiary, classPlan.attendance))
                   count = 0
               }
           }
            count++
        }
        if(classesPlan.size == 0){
            classesPlan.add(ClassPlan("Plano não criado","Este professor não cadastrou nenhum plano de aula nessa disciplina", "", "", ""))
        }
        return classesPlan
    }

    fun parseProfessorEmail(response: String): String{
        Jsoup.parse(response).run {
            var email = getElementsByTag("h2")
            var arr = email[0]
            var splited = arr.text().split("- ", "<")
            return if(splited.size != 1){
                splited[1]
            } else{
                "Email não cadastrado"
            }

        }
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
                    val grade = Grade(it.text(), grades[index].text())
                    gradesList.add(grade)
                    index++
                }
            }
        }
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
        return newsList
    }

    fun parseHorasComplementares(response: String): MutableList<HorasComplementares>{
        var horas = mutableListOf<HorasComplementares>()
        Jsoup.parse(response).run {
            var body = getElementsByTag("td")
            var horaDef = HorasComplementares("", "", "")
            var index = 1
            for(it in body){
                when(index){
                    1 -> {
                        horaDef.name = it.text()
                    }
                    3 -> {
                        horaDef.professor = it.text()
                    }
                    4 -> {
                        horaDef.horas = it.text()
                        horas.add(HorasComplementares(horaDef.name, "Professor: " + horaDef.professor, horaDef.horas))
                        index = 0
                    }
                }
                index++
            }
        }
        var arr = response.split("Total de Horas em Atividades Complementares: ")
        arr = arr[1].split("</h2>")
        horas.add(HorasComplementares("Total de Horas Complementares", " ", arr[0]))
        return horas
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
        if(filesList.size == 0){
            filesList.add(File("Nenhum arquivo disponível nessa disciplina"))
        }
        return filesList
    }
}