package com.rodrigmatrix.sippa

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.github.kittinunf.fuel.httpPost
import com.google.android.material.snackbar.Snackbar
import com.rodrigmatrix.sippa.persistance.Student
import com.rodrigmatrix.sippa.persistance.StudentsDatabase
import okhttp3.*
import java.io.IOException
import java.lang.Exception

class Api {
    fun Context.toast(context: Context = applicationContext, message: String, duration: Int = Toast.LENGTH_SHORT){
        Toast.makeText(context, message , duration).show()
    }
    fun login(login: String, password: String, captcha: String, cookie: String, context: Context, view: View, captcha_image: ImageView, button: Button, database: StudentsDatabase){
        var encoded = "login=" + login + "&senha=" + password + "&conta=aluno&captcha=" + captcha + "&comando=CmdLogin&enviar=Entrar"
        //println("encoded form: " + encoded)
        var code = 0
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
                when {
                    response.toString().contains("Olá ALUNO(A)") -> {
                        val intent = Intent(context, Home::class.java)
                        intent.putExtra("html_response", response.toString())
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
                println(response.toString())
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
                val student = Student(0, jsession, "")
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