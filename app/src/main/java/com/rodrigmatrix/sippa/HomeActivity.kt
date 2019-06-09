package com.rodrigmatrix.sippa

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.room.Room
import com.rodrigmatrix.sippa.persistance.StudentsDatabase
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.nav_header_home.*
import org.jetbrains.anko.configuration
import org.jetbrains.anko.textColor
import kotlinx.coroutines.*


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, HorasFragment.OnFragmentInteractionListener, DisciplinasFragment.OnFragmentInteractionListener {
    private var doubleBackToExitPressedOnce = false
    private var selectedFragment = Fragment()
    private var disciplinasFragment = DisciplinasFragment()
    private var horasFragment = HorasFragment()
    private var infoFragment = InfoFragment()
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        nav_view.setCheckedItem(R.id.disciplinas_select)
        toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(applicationContext, R.color.colorSippa)
        when (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                toolbar.background = ContextCompat.getDrawable(applicationContext, R.color.White)
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                toolbar.background = null
            }
        }
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        title = "Disciplinas"
        val database = Room.databaseBuilder(
            applicationContext,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
        Thread {
            val student = database.studentDao().getStudent()
            runOnUiThread {
                student_name_text.text = student.name
                student_matricula_text.text = student.matricula
                when(student.theme){
                    "light" -> {
                        setDefaultNightMode(MODE_NIGHT_NO)
                        toolbar.background = ContextCompat.getDrawable(applicationContext, R.color.White)
                    }
                    "dark" -> {
                        setDefaultNightMode(MODE_NIGHT_YES)
                    }
                    "automatic" -> {
                        setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
                    }
                }
            }
        }.start()
        dialogPassword(database)
        var fm = supportFragmentManager
        fm.beginTransaction().add(R.id.view_disciplinas, disciplinasFragment).commit()
        fm.beginTransaction().add(R.id.view_disciplinas, horasFragment).hide(horasFragment).commit()
        fm.beginTransaction().add(R.id.view_disciplinas, infoFragment).hide(infoFragment).commit()
        selectedFragment = disciplinasFragment
    }
    private fun dialogPassword(database: StudentsDatabase){
        Thread {
            if(database.studentDao().getStudent().login == ""){
                var student = database.studentDao().getStudent()
                runOnUiThread {
                    var dialog = AlertDialog.Builder(this@HomeActivity)
                    dialog.setTitle("Salvar Dados")
                    dialog.setMessage("Deseja salvar seus dados de login?")
                    dialog.setPositiveButton("Sim") { dialog, which ->
                        var login = intent.getStringExtra("login")
                        var password = intent.getStringExtra("password")
                        student.login = login
                        student.password = password
                        Thread {
                            database.studentDao().delete()
                            database.studentDao().insert(student)
                        }.start()
                    }
                    dialog.setNegativeButton("Agora Não") { dialog, which ->
                    }
                    var alert = dialog.create()
                    alert.show()
                }
            }
            else{
                var student = database.studentDao().getStudent()
                runOnUiThread {
                    var login = intent.getStringExtra("login")
                    var password = intent.getStringExtra("password")
                    if(student.login != login || student.password != password){
                        runOnUiThread{
                            var dialog = AlertDialog.Builder(this@HomeActivity)
                            dialog.setTitle("Atualizar Login")
                            dialog.setMessage("Você fez login com uma nova conta. Deseja atualizar os dados salvos de login?")
                            dialog.setPositiveButton("Atualizar") { dialog, which ->
                                student.login = login
                                student.password = password
                                Thread {
                                    database.studentDao().delete()
                                    database.studentDao().insert(student)
                                }.start()
                            }
                            dialog.setNegativeButton("Agora Não") { dialog, which ->
                            }
                            var alert = dialog.create()
                            alert.show()
                        }
                    }
                }
            }
        }.start()
    }

    override fun onBackPressed(){
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Pressione voltar novamente para sair", Toast.LENGTH_SHORT).show()
        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_theme, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val database = Room.databaseBuilder(
            applicationContext,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
        Thread {
            val student = database.studentDao().getStudent()
            var theme = ""
            runOnUiThread {
                when(item!!.itemId){
                    R.id.light_button ->{
                        setDefaultNightMode(MODE_NIGHT_NO)
                        theme = "light"
                    }
                    R.id.dark_button ->{
                        setDefaultNightMode(MODE_NIGHT_YES)
                        theme = "dark"
                    }
                    R.id.automatic_button ->{
                        setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
                        theme = "automatic"
                    }
                }
            }
            student.theme = theme
            database.studentDao().delete()
            database.studentDao().insert(student)
        }.start()
        return super.onOptionsItemSelected(item)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.disciplinas_select -> {
                title = "Disciplinas"
                toolbar.setTitleTextColor(ContextCompat.getColor(applicationContext, R.color.colorSippa))
//                nav_view.itemTextColor = getColorStateList()
                window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.colorSippa)
                window.navigationBarColor = ContextCompat.getColor(applicationContext, R.color.colorSippa)
                toggle.drawerArrowDrawable.color = ContextCompat.getColor(applicationContext, R.color.colorSippa)
                sistema_name.text = "Sippa"
                this.setTheme(R.style.Sippa)
                sistema_name.textColor = ContextCompat.getColor(applicationContext, R.color.colorSippa)
                student_name_text.textColor = ContextCompat.getColor(applicationContext, R.color.colorSippa)
                student_matricula_text.textColor = ContextCompat.getColor(applicationContext, R.color.colorSippa)
                supportFragmentManager
                    .beginTransaction()
                    .hide(selectedFragment)
                    .show(disciplinasFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
                selectedFragment = disciplinasFragment
            }
            R.id.horas_select -> {
                title = "Horas Complementares"
                window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.colorSisac)
                window.navigationBarColor = ContextCompat.getColor(applicationContext, R.color.colorSisac)
                toolbar.setTitleTextColor(ContextCompat.getColor(applicationContext, R.color.colorSisac))
                toggle.drawerArrowDrawable.color = ContextCompat.getColor(applicationContext, R.color.colorSisac)
                this.setTheme(R.style.Sisac)
                sistema_name.text = "Sisac"
                sistema_name.textColor = ContextCompat.getColor(applicationContext, R.color.colorSisac)
                student_name_text.textColor = ContextCompat.getColor(applicationContext, R.color.colorSisac)
                student_matricula_text.textColor = ContextCompat.getColor(applicationContext, R.color.colorSisac)
                supportFragmentManager
                    .beginTransaction()
                    .hide(selectedFragment)
                    .show(horasFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
                selectedFragment = horasFragment
            }
            R.id.info_select -> {
                title = "Sobre"
                toggle.drawerArrowDrawable.color = ContextCompat.getColor(applicationContext, R.color.colorSippa)
                window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.colorSippa)
                window.navigationBarColor = ContextCompat.getColor(applicationContext, R.color.colorSippa)
                toolbar.setTitleTextColor(ContextCompat.getColor(applicationContext, R.color.colorSippa))
                sistema_name.text = "Sippa"
                sistema_name.textColor = ContextCompat.getColor(applicationContext, R.color.colorSippa)
                student_name_text.textColor = ContextCompat.getColor(applicationContext, R.color.colorSippa)
                student_matricula_text.textColor = ContextCompat.getColor(applicationContext, R.color.colorSippa)
                supportFragmentManager
                    .beginTransaction()
                    .hide(selectedFragment)
                    .show(infoFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
                selectedFragment = infoFragment
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onFragmentInteraction(uri: Uri) {
        println("Interaction")
    }
}
