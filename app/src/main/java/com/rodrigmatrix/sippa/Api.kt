package com.rodrigmatrix.sippa

import android.util.Log
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import org.json.JSONObject
import java.net.URLEncoder
import java.security.Policy

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

    fun login(login: String, password: String, captcha: String, cookie: String){
        val encoded = "login=" + login + "&senha=" + password + "&conta=aluno&captcha=" + captcha + "&comando=CmdLogin&enviar=Entrar"
        println("cookie login: " + cookie)

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
                println(response)
            }
    }
}