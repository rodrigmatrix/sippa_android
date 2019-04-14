package com.rodrigmatrix.sippa

import android.content.Context
import android.widget.EditText
import android.widget.Toast
import com.github.kittinunf.fuel.core.FuelManager.Companion.instance
import com.github.kittinunf.fuel.httpPost
import org.jetbrains.anko.doAsync

class Api {

//    fun get_cookie(): String{
//        var data = ""
//        var jsession = ""
//        "https://sistemas.quixada.ufc.br/apps/sippa/captcha.jpg"
//            .httpGet()
//            .response{ request, response, result ->
//                data = response.headers["Set-Cookie"].toString()
//                println("response cookie " + data)
//                data = data.replace("[","")
//                var parts = data.split(";")
//                jsession = parts[0]
//                println("JSESSION:= " + jsession)
//            }
//
//            return jsession
//
//    }
    fun Context.toast(context: Context = applicationContext, message: String, duration: Int = Toast.LENGTH_SHORT){
        Toast.makeText(context, message , duration).show()
    }
    fun login(login: String, password: String, captcha: String, cookie: String): Int{
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
            .response{
                request, response, result ->
                doAsync { code = response.statusCode }
            }
        //println("code: " + code)
        return code
    }
}