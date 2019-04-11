package com.rodrigmatrix.sippa

import android.util.Log
import com.github.kittinunf.fuel.httpPost
import org.json.JSONObject

class Api {

    fun get(){
        "https://sistemas.quixada.ufc.br/apps/sippa/".httpPost().response{
                request, response, result ->
            Log.d("Message", response.toString())
        }
    }
    fun login(login: String, password: String, captcha: String){
        val bodyJson = JSONObject("""{"name":w, "age":25}""")
        "https://sistemas.quixada.ufc.br/apps/sippa/captcha.jpg"
            .httpPost()
            .header("Content-Type" to "x-www-form-urlencoded")
            .body(bodyJson.toString())
            .response{
                request, response, result ->
            Log.d("Message", response.toString())
        }
    }
}