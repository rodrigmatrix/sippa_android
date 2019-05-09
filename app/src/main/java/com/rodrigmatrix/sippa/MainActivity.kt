package com.rodrigmatrix.sippa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import java.io.IOException
import android.graphics.BitmapFactory
import okhttp3.*
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val loginbtn = findViewById<View>(R.id.login) as Button
        val captcha = findViewById<View>(R.id.captcha_image) as ImageView
        val api = Api()
//        Picasso.get()
//            .load("https://sistemas.quixada.ufc.br/apps/sippa/captcha.jpg")
//            .into(captcha)
        val request = Request.Builder()
            .url("https://sistemas.quixada.ufc.br/apps/sippa/captcha.jpg")
            .build()
        val client = OkHttpClient()
        var data = ""
        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                data = response.header("Set-Cookie").toString()

                println("res mes " + data)
                if(response.body() != null) {
                    var bmp = BitmapFactory.decodeStream(response.body()!!.byteStream())

                    try {
                        if(bmp != null) {
                            captcha.setImageBitmap(bmp)
                        }
                    } catch (e: Exception) {
                        println(e.message)
                    }


                }
            }

            @Throws(IOException::class)
            override fun onFailure(call: Call, e: IOException) {
                println(e.message)
            }
        })

        //getCaptcha(captcha)


        loginbtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                var login = findViewById<EditText>(R.id.login_input)
                var password = findViewById<EditText>(R.id.password_input)
                var captcha_input = findViewById<EditText>(R.id.captcha_input)

                data = data.replace("[","")
                var parts = data.split(";")
                var jsession = parts[0]
                // status 200:  aluno ou senhanao encontrado
                // status 200: erro 500 contacte o adm do sistema (cookie expirado)
                // status 200: erro ao digitar captcha
                // status 200: sucesso
                api.login(login.text.toString(), password.text.toString(), captcha_input.text.toString(), jsession)


            }
        })
    }
}

fun getCaptcha(captcha: ImageView) {

}


