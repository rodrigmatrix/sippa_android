package com.rodrigmatrix.sippa

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.room.Room
import com.github.kittinunf.fuel.Fuel
import com.google.android.material.snackbar.Snackbar
import com.rodrigmatrix.sippa.persistance.Student
import com.rodrigmatrix.sippa.persistance.StudentsDatabase
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    lateinit var cd: ConnectionDetector
    lateinit var loginbtn: Button
    lateinit var captcha_image: ImageView
    lateinit var  progress: ProgressBar
    lateinit var view: View
    lateinit var database: StudentsDatabase
    lateinit var login: EditText
    lateinit var password: EditText
    lateinit var captcha_input: EditText
    lateinit var reload: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        database = Room.databaseBuilder(
            applicationContext,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
        loginbtn = findViewById<View>(R.id.login) as Button
        captcha_image = findViewById<View>(R.id.captcha_image) as ImageView
        progress = findViewById<View>(R.id.progressLogin) as ProgressBar
        view = findViewById<View>(R.id.main_activity)
        cd = ConnectionDetector()
        login = findViewById(R.id.login_input)
        password = findViewById(R.id.password_input)
        captcha_input = findViewById(R.id.captcha_input)
        reload = findViewById(R.id.reload_button)
        setFields()
        reload.isEnabled = false
        getCaptcha()
        loginbtn.setOnClickListener{
            view.hideKeyboard()
            progress.isVisible = true
            loginbtn.isEnabled = false
            reload.isEnabled = false
            Thread {
                var student = database.StudentDao().getStudent()
                if(student != null){
                    login(student.jsession)
                }
                else{
                    login("")
                }
            }.start()
        }
        reload.setOnClickListener {
            progress.isVisible = true
            reload.isEnabled = false
            getCaptcha()
        }
    }
    private fun getCaptcha(){
        if(!cd.isConnectingToInternet(this@MainActivity)){
            runOnUiThread {
                val snackbar = Snackbar.make(view, "Verifique sua conexão com a internet", Snackbar.LENGTH_LONG)
                snackbar.show()
            }
        }
        val request = Request.Builder()
            .url("https://sistemas.quixada.ufc.br/apps/sippa/captcha.jpg")
            .build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                //database.StudentDao().delete()
                var data = response.header("Set-Cookie").toString()
                data = data.replace("[","")
                var parts = data.split(";")
                var jsession = parts[0]
                val student = database.StudentDao().getStudent()
                if(student != null){
                    student.jsession = jsession
                    database.StudentDao().insert(student)
                }
                else{
                    database.StudentDao().insert(
                        Student(0, jsession, "",
                        "", "", "","", "")
                    )
                }

                if(response.body() != null) {
                    var bmp = BitmapFactory.decodeStream(response.body()!!.byteStream())
                    try {
                        if(bmp != null) {
                            runOnUiThread {
                                captcha_input.text.clear()
                                progress.isVisible = false
                                loginbtn.isEnabled = true
                                reload.isEnabled = true
                                captcha_image.setImageBitmap(bmp)
                            }
                        }
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                println(e.message)
                runOnUiThread {
                    captcha_input.text.clear()
                    progress.isVisible = false
                    loginbtn.isEnabled = true
                    reload.isEnabled = true
                    val snackbar = Snackbar.make(view, "O Sippa aparenta estar offline no momento. Tente novamente mais tarde ou verifique sua conexão com a internet", Snackbar.LENGTH_LONG)
                    snackbar.show()
                }
            }
        })
    }
    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
    fun setFields(){
        Thread {
            var student = database.StudentDao().getStudent()
            runOnUiThread {
                if((student != null) && (student.login != "")){
                    login.setText(student.login)
                    password.setText(student.password)
                    password_field.isPasswordVisibilityToggleEnabled = false
                }
            }
        }.start()
    }


    private fun login(cookie: String) {
        var encoded =
            "login=" + login.text.toString() + "&senha=" + password.text.toString() + "&conta=aluno&captcha=" + captcha_input.text.toString() + "&comando=CmdLogin&enviar=Entrar"
        if (!cd.isConnectingToInternet(this@MainActivity)) {
            runOnUiThread {
                progress.isVisible = false
                captcha_input.text.clear()
                loginbtn.isEnabled = true
                reload.isEnabled = true
                val snackbar = Snackbar.make(view, "Verifique sua conexão com a internet", Snackbar.LENGTH_LONG)
                snackbar.show()
            }
        }
        Fuel.post("https://sistemas.quixada.ufc.br/apps/ServletCentral")
            .header("Content-Type" to "application/x-www-form-urlencoded")
            .header("Cookie", cookie)
            .body(encoded)
            .timeout(40000)
            .timeoutRead(40000)
            .response { request, response, result ->
                println(response.statusCode)
                if(response.statusCode != 200){
                    runOnUiThread {
                        progress.isVisible = false
                        captcha_input.text.clear()
                        loginbtn.isEnabled = true
                        reload.isEnabled = true
                        val snackbar = Snackbar.make(view, "O Sippa aparenta estar offline no momento. Tente novamente mais tarde", Snackbar.LENGTH_LONG)
                        snackbar.show()
                    }
                }
                when {
                    response.toString().contains("Olá ALUNO(A)") -> {
                        val student = database.StudentDao().getStudent()
                        var res_array = response.toString().split("Olá ALUNO(A) ")
                        res_array = res_array[1].split("</h1>")
                        var name = res_array[0]
                        student.id = 0
                        student.responseHtml = response.toString()
                        student.classSetHtml = ""
                        student.matricula = login.text.toString().removeRange(0, 1)
                        student.name = name
                        database.StudentDao().insert(student)
                        runOnUiThread {
                            progress.isVisible = false
                            captcha_input.text.clear()
                            loginbtn.isEnabled = true
                            reload.isEnabled = true
                        }
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.putExtra("login", login.text.toString())
                        intent.putExtra("password", password.text.toString())
                        this.startActivity(intent)
                        println("login com sucesso")
                    }
                    response.toString().contains("Erro 500: Contacte o Administrador do Sistema") -> {
                        getCaptcha()
                        runOnUiThread {
                            progress.isVisible = false
                            captcha_input.text.clear()
                            loginbtn.isEnabled = true
                            reload.isEnabled = true
                            val snackbar = Snackbar.make(
                                view,
                                "Tempo de conexão expirado. Digite o novo captcha",
                                Snackbar.LENGTH_LONG
                            )
                            snackbar.show()
                        }
                        println("Error 500 contacte")
                    }
                    response.toString().contains("Preencha todos os campos.") -> {
                        getCaptcha()
                        runOnUiThread {
                            progress.isVisible = false
                            captcha_input.text.clear()
                            loginbtn.isEnabled = true
                            reload.isEnabled = true
                            val snackbar = Snackbar.make(view, "Preencha todos os dados de login", Snackbar.LENGTH_LONG)
                            snackbar.show()
                        }
                        println("Error 500 contacte")
                    }
                    response.toString().contains("Erro ao digitar os caracteres. Por favor, tente novamente.") -> {
                        getCaptcha()
                        runOnUiThread {
                            progress.isVisible = false
                            captcha_input.text.clear()
                            loginbtn.isEnabled = true
                            reload.isEnabled = true
                            val snackbar =
                                Snackbar.make(view, "Captcha incorreto. Digite o novo captcha", Snackbar.LENGTH_LONG)
                            snackbar.show()
                        }
                        println("Captcha incorreto")
                    }
                    response.toString().contains("Aluno não encontrado ou senha inválida.") -> {
                        getCaptcha()
                        runOnUiThread {
                            progress.isVisible = false
                            captcha_input.text.clear()
                            loginbtn.isEnabled = true
                            reload.isEnabled = true
                            val snackbar = Snackbar.make(view, "Aluno ou senha não encontrados", Snackbar.LENGTH_LONG)
                            snackbar.show()
                            println("Aluno senha incorreto")
                        }
                    }
                }
            }
    }
}
