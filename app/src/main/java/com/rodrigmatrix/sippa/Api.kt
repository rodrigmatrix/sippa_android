package com.rodrigmatrix.sippa

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.google.android.material.snackbar.Snackbar
import com.rodrigmatrix.sippa.persistance.Student
import com.rodrigmatrix.sippa.persistance.StudentsDatabase
import okhttp3.*
import java.io.IOException
import java.lang.Exception


class Api {

    fun login(login: String, password: String, captcha: EditText, cookie: String, context: Context, view: View, captcha_image: ImageView, database: StudentsDatabase){
        var encoded = "login=" + login + "&senha=" + password + "&conta=aluno&captcha=" + captcha.text.toString() + "&comando=CmdLogin&enviar=Entrar"
        //TODO verificar conexao com internet
        Fuel.post("https://sistemas.quixada.ufc.br/apps/ServletCentral")
            .timeout(50000)
            .timeoutRead(60000)
            .header("Content-Type" to "application/x-www-form-urlencoded")
            .header("Cookie", cookie)
            .body(encoded)
            .timeout(50000)
            .timeoutRead(60000)
            .response{ request, response, result ->
                //progress.isVisible = false
                when {
                    response.toString().contains("Olá ALUNO(A)") -> {
                        getHorasComplementares(database)
                        val student = database.StudentDao().getStudent()
                        var res_array = response.toString().split("Olá ALUNO(A) ")
                        res_array = res_array[1].split("</h1>")
                        var name = res_array[0]
                        student.id = 0
                        student.responseHtml = response.toString()
                        student.matricula = login.removeRange(0, 1)
                        student.name = name
                        database.StudentDao().insert(student)
                        val intent = Intent(context, Home::class.java)
                        context.startActivity(intent)
                        println("login com sucesso")
                    }
                    response.toString().contains("Erro 500: Contacte o Administrador do Sistema") -> {
                        //captcha.text.clear()
                        getCaptcha(database, captcha_image)
                        val snackbar = Snackbar.make(view, "Tempo de conexão expirado. Digite o novo captcha", Snackbar.LENGTH_LONG)
                        snackbar.show()
                        println("Error 500 contacte")

                    }
                    response.toString().contains("Preencha todos os campos.") -> {
                        //captcha.text.clear()
                        getCaptcha(database, captcha_image)
                        val snackbar = Snackbar.make(view, "Preencha todos os dados de login", Snackbar.LENGTH_LONG)
                        snackbar.show()

                        println("Error 500 contacte")
                    }
                    response.toString().contains("Erro ao digitar os caracteres. Por favor, tente novamente.") -> {
                        //captcha.text.clear()
                        getCaptcha(database, captcha_image)
                        val snackbar = Snackbar.make(view, "Captcha incorreto. Digite o novo captcha", Snackbar.LENGTH_LONG)
                        snackbar.show()
                        println("Captcha incorreto")
                    }
                    response.toString().contains("Aluno não encontrado ou senha inválida.") -> {
                        //captcha.text.clear()
                        getCaptcha(database, captcha_image)
                        val snackbar = Snackbar.make(view, "Aluno ou senha não encontrados", Snackbar.LENGTH_LONG)
                        snackbar.show()
                        println("Aluno senha incorreto")
                    }
                }
                //println(response.toString())
            }
    }

    fun setClass(id: String, database: StudentsDatabase){
        Fuel.get("https://sistemas.quixada.ufc.br/apps/ServletCentral", listOf("comando" to "CmdListarFrequenciaTurmaAluno", "id" to id))
            .timeout(50000)
            .timeoutRead(60000)
            .header("Content-Type" to "application/x-www-form-urlencoded")
            .header("Cookie", database.StudentDao().getStudent().jsession)
            .timeout(50000)
            .timeoutRead(60000)
            .response{ request, response, result ->
                var student = database.StudentDao().getStudent()
                database.StudentDao().delete()
                student.responseHtml = response.toString()
                database.StudentDao().insert(Student(student.id, student.jsession, response.toString(), student.name, student.matricula))
                // ta inserindo certo no db mas a response n funciona
                //println(database.StudentDao().getStudent().responseHtml)
                //println(res)
                //println("response: " + response.body().toString())
                //println("response: " + response.toString())
            }
        //n funfa :(
        //println(res)
    }
    fun getFiles(database: StudentsDatabase){
        //Somente pode ser usada apos consultar frequencia
        "https://sistemas.quixada.ufc.br/apps/sippa/aluno_visualizar_arquivos.jsp?sorter=1"
            .httpGet()
            .timeout(50000)
            .timeoutRead(60000)
            .header("Content-Type" to "application/x-www-form-urlencoded")
            .header("Cookie", database.StudentDao().getStudent().jsession)
            .timeout(50000)
            .timeoutRead(60000)
            .response{ request, response, result ->
                var student = database.StudentDao().getStudent()
                database.StudentDao().delete()
                student.responseHtml = response.toString()
                database.StudentDao().insert(student)
                //println("response: " + response.body().toString())
                //println("response: " + response.toString())
            }
    }
    fun downloadFile(name: String, database: StudentsDatabase){
        "https://sistemas.quixada.ufc.br/apps/sippa/ServletCentral?comando=CmdLoadArquivo&id="+name
            .httpGet()
            .timeout(50000)
            .timeoutRead(60000)
            .header("Content-Type" to "application/x-www-form-urlencoded")
            .header("Cookie", database.StudentDao().getStudent().jsession)
            .timeout(50000)
            .timeoutRead(60000)
            .response{ request, response, result ->
                //println("response: " + response.body().toString())
                //println("response: " + response)
            }

    }
    fun getGrades(database: StudentsDatabase){
        //Somente pode ser usada apos consultar frequencia
        Fuel.get("https://sistemas.quixada.ufc.br/apps/ServletCentral", listOf("comando" to "CmdVisualizarAvaliacoesAluno"))
            .timeout(50000)
            .timeoutRead(60000)
            .header("Content-Type" to "application/x-www-form-urlencoded")
            .header("Cookie", database.StudentDao().getStudent().jsession)
            .timeout(50000)
            .timeoutRead(60000)
            .response{ request, response, result ->
                var student = database.StudentDao().getStudent()
                database.StudentDao().delete()
                student.responseHtml = response.toString()
                database.StudentDao().insert(student)
                //println("horas complementares: " + response.body().toString())
                //println("horas complementares: " + response)
            }
    }
    fun getHorasComplementares(database: StudentsDatabase){
        Fuel.get("https://sistemas.quixada.ufc.br/apps/ServletCentral", listOf("comando" to "CmdLoginSisacAluno"))
            .timeout(50000)
            .timeoutRead(60000)
            .header("Content-Type" to "application/x-www-form-urlencoded")
            .header("Cookie", database.StudentDao().getStudent().jsession)
            .timeout(50000)
            .timeoutRead(60000)
            .response{ request, response, result ->
                //println("response: " + response.body().toString())
                //println("response: " + response)
            }
    }

    fun getCaptcha(database: StudentsDatabase, captcha_image: ImageView){
        val request = Request.Builder()
            .url("https://sistemas.quixada.ufc.br/apps/sippa/captcha.jpg")
            .build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                database.StudentDao().delete()
                var data = response.header("Set-Cookie").toString()
                data = data.replace("[","")
                var parts = data.split(";")
                var jsession = parts[0]
                val student = Student(0, jsession, "", "", "")
                database.StudentDao().insert(student)
                //println("COOKIE " + jsession)
                if(response.body() != null) {
                    var bmp = BitmapFactory.decodeStream(response.body()!!.byteStream())
                    try {
                        if(bmp != null) {
                            captcha_image.setImageBitmap(bmp)

                        }
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }
            }
            //@Throws(IOException::class)
            override fun onFailure(call: Call, e: IOException) {
                println(e.message)
            }
        })
    }
}