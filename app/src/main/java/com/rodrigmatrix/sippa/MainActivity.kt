package com.rodrigmatrix.sippa

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.core.view.isVisible
import androidx.room.Room
import com.github.kittinunf.fuel.Fuel
import com.google.android.material.snackbar.Snackbar
import com.rodrigmatrix.sippa.persistance.Student
import com.rodrigmatrix.sippa.persistance.StudentsDatabase
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException


class MainActivity : AppCompatActivity() {
    lateinit var cd: ConnectionDetector
    lateinit var database: StudentsDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        database = Room.databaseBuilder(
            applicationContext,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        Thread{
            var student = database.studentDao().getStudent()
            if(student != null){
                runOnUiThread {
                    when (student.theme) {
                        "light" -> {
                            setDefaultNightMode(MODE_NIGHT_NO)
                        }
                        "dark" -> {
                            setDefaultNightMode(MODE_NIGHT_YES)
                        }
                        "automatic" -> {
                            setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
                        }
                    }
                }
            }
        }.start()
        setContentView(R.layout.activity_main)
        cd = ConnectionDetector()
        setFields()
        reload_button.isEnabled = false
        getCaptcha()
        login_btn.setOnClickListener{
            main_activity.hideKeyboard()
            if(isValidLoginAndPass()){
                progressLogin.isVisible = true
                login_btn.isEnabled = false
                reload_button.isEnabled = false
                Thread {
                    var student = database.studentDao().getStudent()
                    if(student != null){
                        login(student.jsession)
                    }
                    else{
                        login("")
                    }
                }.start()
            }
        }
        reload_button.setOnClickListener {
            progressLogin.isVisible = true
            reload_button.isEnabled = false
            getCaptcha()
        }
    }

        private fun getCaptcha(){
        if(!cd.isConnectingToInternet(this@MainActivity)){
            showError("Verifique sua conexão com a internet")
            return
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
                val student = database.studentDao().getStudent()
                if(student != null){
                    student.jsession = jsession
                    database.studentDao().insert(student)
                }
                else{
                    database.studentDao().insert(
                        Student(0, jsession, "",
                        "", "", "","", "","automatic")
                    )
                }
                if(response.body() != null) {
                    var bmp = BitmapFactory.decodeStream(response.body()!!.byteStream())
                    try {
                        if(bmp != null) {
                            runOnUiThread {
                                captcha_input.text!!.clear()
                                progressLogin.isVisible = false
                                login_btn.isEnabled = true
                                reload_button.isEnabled = true
                                captcha_image.setImageBitmap(bmp)
                            }
                        }
                    } catch (e: Throwable) {
                        println(e.message)
                    }
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                println(e.message)
                showError("Verifique se o Sippa está online no momento ou sua conexão com a internet.")
            }
        })
    }
    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
    private fun setFields(){
        Thread {
            var student = database.studentDao().getStudent()
            runOnUiThread {
                if((student != null) && (student.login != "")){
                    login_input.setText(student.login)
                    password_input.setText(student.password)
                }
            }
        }.start()
    }

    private fun isValidLoginAndPass(): Boolean{
        var isValid = true
        if(login_input.text.toString().isEmpty()){
            login_field.error = "Digite seu login"
            isValid = false
        }
        else{
            login_field.error = null
        }
        if(password_input.text.toString().isEmpty()){
            password_field.error = "Digite sua senha"
            isValid = false
        }
        else{
            password_field.error = null
        }
        if(captcha_input.text.toString().isEmpty()){
            captcha.error = "Vazio"
            isValid = false
        }
        else{
            captcha.error = null
        }
        return isValid
    }

    private fun login(cookie: String) {
        var login = login_input.text.toString()
        login.replace("&", "%26")
        login.replace("=", "%3D")
        var password = password_input.text.toString()
        password = password.replace("&", "%26")
        password = password.replace("=", "%3D")
        var encoded = "login=" + login + "&senha=" + password + "&conta=aluno&captcha=" + captcha_input.text.toString() + "&comando=CmdLogin&enviar=Entrar"
        if(!cd.isConnectingToInternet(this@MainActivity)){
            showError("Verifique sua conexão com a internet")
            return
        }
        Fuel.post("https://sistemas.quixada.ufc.br/apps/ServletCentral")
            .header("Content-Type" to "application/x-www-form-urlencoded")
            .header("Cookie", cookie)
            .body(encoded)
            .timeout(60000)
            .timeoutRead(60000)
            .response { _, response, _ ->
                println(response.statusCode)
                if(response.statusCode != 200){
                    showError("Verifique se o Sippa está online no momento ou sua conexão com a internet.")
                }
                when {
                    response.toString().contains("Olá ALUNO(A)") -> {
                        if(response.toString().contains("Por favor, altere a sua senha.")){
                            val client = OkHttpClient()
                            val request = Request.Builder()
                                .url("https://sistemas.quixada.ufc.br/apps/ServletCentral?comando=CmdListarDisciplinaAluno")
                                .header("Content-Type", "application/x-www-form-urlencoded")
                                .header("Cookie", cookie)
                                .build()
                            var response = client.newCall(request).execute()
                            if (response.isSuccessful) {
                                val res = response.body()!!.string()
                                runOnUiThread {
                                    var dialog = AlertDialog.Builder(this@MainActivity)
                                    dialog.setTitle("Senha muito fraca!")
                                    dialog.setMessage("Sua senha é vulnerável. Atualize sua senha do sistema.")
                                    dialog.setPositiveButton("Continuar") { _, _ ->
                                        setData(res)
                                    }
                                    dialog.setNegativeButton("Cancelar") { _, _ ->
                                        getCaptcha()
                                        showError("Login cancelado. Por favor atualize sua senha do sippa")
                                    }
                                    dialog.setOnCancelListener {
                                        getCaptcha()
                                        showError("Login cancelado. Por favor atualize sua senha do sippa")
                                    }
                                    var alert = dialog.create()
                                    alert.show()
                                }
                            }
                            else{
                                showError("Erro ao efetuar login. Verifique sua conexão com a internet")
                            }
                        }
                        else{
                            setData(response.toString())
                        }
                    }
                    response.toString().contains("Erro 500: Contacte o Administrador do Sistema") -> {
                        getCaptcha()
                        showError("Tempo de conexão expirado. Digite o novo captcha")
                    }

                    response.toString().contains("Erro ao digitar os caracteres. Por favor, tente novamente.") -> {
                        getCaptcha()
                        showError("Captcha incorreto. Digite o novo captcha")
                    }

                    response.toString().contains("Aluno não encontrado ou senha inválida.") -> {
                        getCaptcha()
                        showError("Aluno não encontrado ou senha inválida.")
                    }
                }
            }
    }
    private fun showError(error: String){
        runOnUiThread {
            progressLogin.isVisible = false
            captcha_input.text!!.clear()
            login_btn.isEnabled = true
            reload_button.isEnabled = true
            Snackbar.make(main_activity, error, Snackbar.LENGTH_LONG).show()
        }
    }
    private fun setData(response: String){
        val student = database.studentDao().getStudent()
        var resArray = response.split("Olá ALUNO(A) ")
        resArray = resArray[1].split("</h1>")
        var name = resArray[0]
        student.id = 0
        student.responseHtml = response
        student.classSetHtml = ""
        student.matricula = login_input.text.toString().removeRange(0, 1)
        student.name = name
        database.studentDao().insert(student)
        runOnUiThread {
            progressLogin.isVisible = false
            captcha_input.text!!.clear()
            login_btn.isEnabled = true
            reload_button.isEnabled = true
        }
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("login", login_input.text.toString())
        intent.putExtra("password", password_input.text.toString())
        this.startActivity(intent)
        println("login com sucesso")
    }
}
