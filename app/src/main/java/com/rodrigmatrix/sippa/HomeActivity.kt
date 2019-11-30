package com.rodrigmatrix.sippa

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.room.Room
import com.rodrigmatrix.sippa.persistence.StudentsDatabase
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.nav_header_home.*
import org.jetbrains.anko.configuration
import org.jetbrains.anko.textColor
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import com.rodrigmatrix.sippa.fragments.DisciplinasFragment


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, HorasFragment.OnFragmentInteractionListener, CoroutineScope {
    private var doubleBackToExitPressedOnce = false
    private var selectedFragment = Fragment()
    private lateinit var disciplinasFragment: Fragment
    lateinit var horasFragment: Fragment
    private lateinit var infoFragment: Fragment
    private lateinit var toggle: ActionBarDrawerToggle
    lateinit var fragmentManager: FragmentManager
    lateinit var database: StudentsDatabase
    private var job: Job = Job()
    lateinit var theme: String
    override val coroutineContext: CoroutineContext get() = Dispatchers.IO + job
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        nav_view.setCheckedItem(R.id.disciplinas_select)
        database = Room.databaseBuilder(
            applicationContext,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(applicationContext, R.color.colorSippa)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        title = "Disciplinas"
        disciplinasFragment = DisciplinasFragment()
        horasFragment = HorasFragment.newInstance()
        infoFragment = InfoFragment()
        val student = database.studentDao().getStudent()
        when (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                theme = "light"
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                theme = "dark"
            }
        }
        if(student.jsession != "offline"){
            dialogPassword(database)
        }
        fragmentManager = supportFragmentManager
        for(fragment in fragmentManager.fragments){
            fragmentManager.beginTransaction().remove(fragment).commit()
        }
        fragmentManager.beginTransaction().add(R.id.view_disciplinas, disciplinasFragment).commit()
        fragmentManager.beginTransaction().add(R.id.view_disciplinas, horasFragment).hide(horasFragment).commit()
        fragmentManager.beginTransaction().add(R.id.view_disciplinas, infoFragment).hide(infoFragment).commit()
        selectedFragment = disciplinasFragment
        launch(handler) {
            runOnUiThread {
                nav_view.getHeaderView(0).findViewById<TextView>(R.id.student_name_text).text = "Olá ${student.name.split(" ")[0]}"
                nav_view.getHeaderView(0).findViewById<TextView>(R.id.student_matricula_text).text = student.matricula
            }
        }
    }
    private fun dialogPassword(database: StudentsDatabase){
        if(database.studentDao().getStudent().login == ""){
            runOnUiThread {
                var dialog = AlertDialog.Builder(this@HomeActivity)
                dialog.setTitle("Salvar Dados")
                dialog.setMessage("Deseja salvar seus dados de login?")
                dialog.setPositiveButton("Sim") { _, _ ->
                    var student = database.studentDao().getStudent()
                    var login = intent.getStringExtra("login")
                    var password = intent.getStringExtra("password")
                    student.login = login
                    student.password = password
                    database.studentDao().deleteStudent()
                    database.studentDao().insertStudent(student)
                }
                dialog.setNegativeButton("Agora Não") { _, _ ->
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
                        dialog.setPositiveButton("Atualizar") { _, _ ->
                            var student2 = database.studentDao().getStudent()
                            student2.login = login
                            student2.password = password
                            database.studentDao().deleteStudent()
                            database.studentDao().insertStudent(student2)
                        }
                        dialog.setNegativeButton("Agora Não") { _, _ ->
                        }
                        var alert = dialog.create()
                        alert.show()
                    }
                }
            }
        }
    }
    private val handler = CoroutineExceptionHandler { _, throwable ->
        Log.e("Exception", ":$throwable")
    }
    override fun onStop() {
        nav_view?.setCheckedItem(R.id.disciplinas_select)
        job.cancel()
        super.onStop()
    }
    override fun onDestroy() {
        nav_view?.setCheckedItem(R.id.disciplinas_select)
        job.cancel()
        super.onDestroy()
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
        val student = database.studentDao().getStudent()
        when(item!!.itemId){
            R.id.light_button ->{
                student.theme = "light"
                database.studentDao().deleteStudent()
                database.studentDao().insertStudent(student)
                runOnUiThread {
                    setDefaultNightMode(MODE_NIGHT_NO)
                }
            }
            R.id.dark_button ->{
                student.theme = "dark"
                database.studentDao().deleteStudent()
                database.studentDao().insertStudent(student)
                runOnUiThread {
                    setDefaultNightMode(MODE_NIGHT_YES)
                }
            }
            R.id.automatic_button ->{
                student.theme = "automatic"
                database.studentDao().deleteStudent()
                database.studentDao().insertStudent(student)
                runOnUiThread {
                    setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setThemeOption(option: Int){
        when(option){
            1 ->{
                title = "Disciplinas"
                val color = ContextCompat.getColor(this, R.color.colorSippa)
                toolbar.setTitleTextColor(color)
                toggle.drawerArrowDrawable.color = color
                if((theme != "dark") && (Build.VERSION.SDK_INT < 27)){
                    window.statusBarColor = color
                    window.navigationBarColor = color
                }
                sistema_name.text = "Sippa"
                sistema_name.textColor = color
                student_name_text.textColor = color
                student_matricula_text.textColor = color
            }
            2 ->{
                title = "Horas Complementares"
                val color = ContextCompat.getColor(this, R.color.colorSisac)
                toolbar.setTitleTextColor(color)
                if((theme != "dark") && (Build.VERSION.SDK_INT < 27)){
                    window.statusBarColor = color
                    window.navigationBarColor = color
                }
                toggle.drawerArrowDrawable.color = color
                sistema_name.text = "Sisac"
                sistema_name.textColor = color
                student_name_text.textColor = color
                student_matricula_text.textColor = color
            }
            3 ->{
                title = "Sobre"
                val color = ContextCompat.getColor(this, R.color.colorSippa)
                toolbar.setTitleTextColor(color)
                if((theme != "dark") && (Build.VERSION.SDK_INT < 27)){
                    window.statusBarColor = color
                    window.navigationBarColor = color
                }
                toggle.drawerArrowDrawable.color = color
                sistema_name.text = "Sippa"
                sistema_name.textColor = color
                student_name_text.textColor = color
                student_matricula_text.textColor = color
            }
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.disciplinas_select -> {
                setThemeOption(1)
                supportFragmentManager
                    .beginTransaction()
                    .hide(selectedFragment)
                    .show(disciplinasFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
                selectedFragment = disciplinasFragment
            }
            R.id.horas_select -> {
                setThemeOption(2)
                supportFragmentManager
                    .beginTransaction()
                    .hide(selectedFragment)
                    .show(horasFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
                selectedFragment = horasFragment
            }
            R.id.info_select -> {
                setThemeOption(3)
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
