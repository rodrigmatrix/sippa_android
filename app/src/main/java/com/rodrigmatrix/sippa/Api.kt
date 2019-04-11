package com.rodrigmatrix.sippa

import android.util.Log
import com.github.kittinunf.fuel.httpPost

class Api {

    fun get(){
        "https://sistemas.quixada.ufc.br/apps/sippa/".httpPost().response{
                request, response, result ->
            Log.d("Message", response.toString())
        }
    }
}