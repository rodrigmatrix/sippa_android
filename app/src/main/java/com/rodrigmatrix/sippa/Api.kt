package com.rodrigmatrix.sippa

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.google.android.material.snackbar.Snackbar
import com.rodrigmatrix.sippa.persistance.Student
import com.rodrigmatrix.sippa.persistance.StudentsDatabase
import okhttp3.*
import java.io.IOException
import java.lang.Exception


class Api {

    fun login(login: String, password: String, captcha: String, cookie: String, context: Context, view: View, captcha_image: ImageView, button: Button, database: StudentsDatabase){
        var encoded = "login=" + login + "&senha=" + password + "&conta=aluno&captcha=" + captcha + "&comando=CmdLogin&enviar=Entrar"
        //println("encoded form: " + encoded)
        //TODO verificar conexao com internet
        "https://sistemas.quixada.ufc.br/apps/ServletCentral"
            .httpPost()
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
                        var lines = response.toString().lines()
                        //println(lines[102])
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
                        //println("student dao: " + database.StudentDao().getJsession())
                        //println("student data: " + student.name + " " + student.matricula)
                        val intent = Intent(context, Home::class.java)
                        context.startActivity(intent)
                        println("login com sucesso")
                    }
                    response.toString().contains("Erro 500: Contacte o Administrador do Sistema") -> {
                        getCaptcha(database, captcha_image)
                        val snackbar = Snackbar.make(view, "Tempo de conexão expirado. Digite o novo captcha", Snackbar.LENGTH_LONG)
                        snackbar.show()
                        println("Error 500 contacte")

                    }
                    response.toString().contains("Preencha todos os campos.") -> {
                        getCaptcha(database, captcha_image)
                        val snackbar = Snackbar.make(view, "Preencha todos os dados de login", Snackbar.LENGTH_LONG)
                        snackbar.show()

                        println("Error 500 contacte")
                    }
                    response.toString().contains("Erro ao digitar os caracteres. Por favor, tente novamente.") -> {
                        getCaptcha(database, captcha_image)
                        val snackbar = Snackbar.make(view, "Captcha incorreto. Digite o novo captcha", Snackbar.LENGTH_LONG)
                        snackbar.show()
                        println("Captcha incorreto")
                    }
                    response.toString().contains("Aluno não encontrado ou senha inválida.") -> {
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
        "https://sistemas.quixada.ufc.br/apps/ServletCentral?comando=CmdListarFrequenciaTurmaAluno&id=" + id
            .httpGet()
            .timeout(50000)
            .timeoutRead(60000)
            .header("Content-Type" to "application/x-www-form-urlencoded")
            .header("Cookie", database.StudentDao().getStudent().jsession)
            .timeout(50000)
            .timeoutRead(60000)
            .response{ request, response, result ->
                println("response: " + response.body().toString())
                println("response: " + response)
            }
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
                println("response: " + response.body().toString())
                println("response: " + response)
            }
    }
    fun getGrades(database: StudentsDatabase): String{
        //Somente pode ser usada apos consultar frequencia
        var res = ""
        "https://sistemas.quixada.ufc.br/apps/ServletCentral?comando=CmdVisualizarAvaliacoesAluno"
            .httpGet()
            .timeout(50000)
            .timeoutRead(60000)
            .header("Content-Type" to "application/x-www-form-urlencoded")
            .header("Cookie", database.StudentDao().getStudent().jsession)
            .timeout(50000)
            .timeoutRead(60000)
            .response{ request, response, result ->
                res = response.toString()
                println("horas complementares: " + response.body().toString())
                println("horas complementares: " + response)
            }
        return res
    }
    fun getHorasComplementares(database: StudentsDatabase){
        "https://sistemas.quixada.ufc.br/apps/ServletCentral?comando=CmdLoginSisacAluno"
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
                val student = Student(0, jsession, "", "", "", "")
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