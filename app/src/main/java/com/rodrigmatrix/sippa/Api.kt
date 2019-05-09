package com.rodrigmatrix.sippa

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.github.kittinunf.fuel.httpPost
import com.google.android.material.snackbar.Snackbar
import okhttp3.*
import java.io.IOException
import java.lang.Exception

class Api {
    fun Context.toast(context: Context = applicationContext, message: String, duration: Int = Toast.LENGTH_SHORT){
        Toast.makeText(context, message , duration).show()
    }
    fun login(login: String, password: String, captcha: String, cookie: String, context: Context, view: View, captcha_image: ImageView){
        var encoded = "login=" + login + "&senha=" + password + "&conta=aluno&captcha=" + captcha + "&comando=CmdLogin&enviar=Entrar"
        //println("encoded form: " + encoded)
        var code = 0
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
                        //println("login com sucesso")
                    }
                    response.toString().contains("Erro 500: Contacte o Administrador do Sistema") -> {
                        val request = Request.Builder()
                            .url("https://sistemas.quixada.ufc.br/apps/sippa/captcha.jpg")
                            .build()
                        val client = OkHttpClient()
                        var jsession = "error"
                        client.newCall(request).enqueue(object : Callback {
                            override fun onResponse(call: Call, response: Response) {
                                var data = response.header("Set-Cookie").toString()
                                data = data.replace("[","")
                                var parts = data.split(";")
                                jsession = parts[0]
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
                        val snackbar = Snackbar.make(view, "Tempo de conexão expirado. Digite o novo captcha", Snackbar.LENGTH_LONG)
                        snackbar.show()

                        //println("Error 500 contacte")
                    }
                    response.toString().contains("Erro ao digitar os caracteres. Por favor, tente novamente.") -> {
                        val request = Request.Builder()
                            .url("https://sistemas.quixada.ufc.br/apps/sippa/captcha.jpg")
                            .build()
                        val client = OkHttpClient()
                        var jsession = "error"
                        client.newCall(request).enqueue(object : Callback {
                            override fun onResponse(call: Call, response: Response) {
                                var data = response.header("Set-Cookie").toString()
                                data = data.replace("[","")
                                var parts = data.split(";")
                                jsession = parts[0]
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
                        val snackbar = Snackbar.make(view, "Captcha incorreto. Digite o novo captcha", Snackbar.LENGTH_LONG)
                        snackbar.show()
                        //println("Captcha incorreto")
                    }
                    response.toString().contains("Aluno não encontrado ou senha inválida.") -> {
                        val request = Request.Builder()
                            .url("https://sistemas.quixada.ufc.br/apps/sippa/captcha.jpg")
                            .build()
                        val client = OkHttpClient()
                        var jsession = "error"
                        client.newCall(request).enqueue(object : Callback {
                            override fun onResponse(call: Call, response: Response) {
                                var data = response.header("Set-Cookie").toString()
                                data = data.replace("[","")
                                var parts = data.split(";")
                                jsession = parts[0]
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
                        val snackbar = Snackbar.make(view, "Aluno ou senha não encontrados", Snackbar.LENGTH_LONG)
                        snackbar.show()
                        //println("Aluno senha incorreto")
                    }
                    //println(response.toString())
                }
                //println(response.toString())
            }

    }
}