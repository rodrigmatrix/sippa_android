package com.rodrigmatrix.sippa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import org.jetbrains.anko.doAsync
import androidx.room.Room
import com.rodrigmatrix.sippa.persistance.StudentsDatabase


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val loginbtn = findViewById<View>(R.id.login) as Button
        val captcha_image = findViewById<View>(R.id.captcha_image) as ImageView
        val api = Api()
        val database = Room.databaseBuilder(
            applicationContext,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
        api.getCaptcha(database, captcha_image)
        loginbtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                var login = findViewById<EditText>(R.id.login_input)
                var password = findViewById<EditText>(R.id.password_input)
                var captcha_input = findViewById<EditText>(R.id.captcha_input)
                doAsync {
                    var jsession = database.StudentDao().getJsession().jsession
                    api.login(login.text.toString(), password.text.toString(), captcha_input.text.toString(), jsession, this@MainActivity, findViewById(R.id.activity_main), captcha_image, loginbtn, database)
                }

            }
        })
    }

}

