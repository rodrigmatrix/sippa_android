package com.rodrigmatrix.sippa

import android.content.Context
import android.widget.Toast
import com.github.kittinunf.fuel.httpPost

class Api {
    fun Context.toast(context: Context = applicationContext, message: String, duration: Int = Toast.LENGTH_SHORT){
        Toast.makeText(context, message , duration).show()
    }
    fun login(login: String, password: String, captcha: String, cookie: String){
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
                if(response.toString().contains("Olá ALUNO(A)")){
                    println("login com sucesso")
                }
                else if(response.toString().contains("Erro 500: Contacte o Administrador do Sistema")){
                    println("Error 500 contacte")
                }
                else if(response.toString().contains("Erro ao digitar os caracteres. Por favor, tente novamente.")){
                    println("Captcha incorreto")
                }
                else if(response.toString().contains("Aluno não encontrado ou senha inválida.")){
                    println("Aluno senha incorreto")
                }
                //println(response.toString())
            }

    }
}