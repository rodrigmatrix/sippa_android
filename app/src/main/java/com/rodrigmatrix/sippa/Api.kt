package com.rodrigmatrix.sippa

import android.util.Log
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.httpPost
import org.json.JSONObject
import java.net.URLEncoder
import java.security.Policy

class Api {

    fun get(){
        "https://sistemas.quixada.ufc.br/apps/sippa/".httpPost().response{
                request, response, result ->
           // Log.d("Message", response.toString())
        }
    }
    fun login(login: String, password: String, captcha: String){
        val encoded = "login=0421757&senha=iphone5s&conta=aluno&captcha=a5ue&comando=CmdLogin&enviar=Entrar"
        "https://sistemas.quixada.ufc.br/apps/ServletCentral"
            .httpPost()
            .timeout(5000)
            .header("Content-Type" to "application/x-www-form-urlencoded")
            .header("Cookie", "JSESSIONID=1E606D44155FA6FAD571AA793B8B6A02")
            .body(encoded)
            .timeout(5000)
            .response{
                request, response, result ->
            Log.d("the result is", result.toString())
            Log.d("the response is", response.toString())
        }
    }
}