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
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val loginbtn = findViewById<View>(R.id.login) as Button
        val captcha_image = findViewById<View>(R.id.captcha_image) as ImageView
        val api = Api()
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
        loginbtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                var login = findViewById<EditText>(R.id.login_input)
                var password = findViewById<EditText>(R.id.password_input)
                var captcha_input = findViewById<EditText>(R.id.captcha_input)

                api.login(login.text.toString(), password.text.toString(), captcha_input.text.toString(), jsession, this@MainActivity, findViewById(R.id.activity_main), captcha_image)
            }
        })
    }
}

fun getCaptcha(captcha: ImageView): String {
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
            println("COOKIE " + jsession)
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
        //@Throws(IOException::class)
        override fun onFailure(call: Call, e: IOException) {
            println(e.message)
        }
    })
    return jsession
}
