package com.rodrigmatrix.sippa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import androidx.room.Room
import com.rodrigmatrix.sippa.Serializer.Serializer
import com.rodrigmatrix.sippa.persistance.StudentsDatabase
import org.jetbrains.anko.doAsync

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val loginbtn = findViewById<View>(R.id.login) as Button
        val captcha_image = findViewById<View>(R.id.captcha_image) as ImageView
        //val progress = findViewById<View>(R.id.progressLogin) as ProgressBar
        //progress.isVisible = false
        val api = Api()
        val database = Room.databaseBuilder(
            applicationContext,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
        api.getCaptcha(database, captcha_image)
        var login = findViewById<EditText>(R.id.login_input)
        var password = findViewById<EditText>(R.id.password_input)
        var captcha_input = findViewById<EditText>(R.id.captcha_input)
        var view = findViewById<View>(R.id.main_activity)

        loginbtn.setOnClickListener{
            //progress.isVisible = true
            val thread = Thread {
                //api.setClass("", database)
                var jsession = database.StudentDao().getStudent().jsession
                api.login(login.text.toString(), password.text.toString(), captcha_input.text.toString(), jsession, this@MainActivity, view, captcha_image, loginbtn, database)
            }
            thread.start()
        }
    }
}
