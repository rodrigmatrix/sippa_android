package com.rodrigmatrix.sippa

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import com.rodrigmatrix.sippa.persistance.Student
import com.rodrigmatrix.sippa.persistance.StudentsDatabase
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.nav_header_home.*
import org.jetbrains.anko.colorAttr
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toolbar


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
        this.onBackPressed()
        nav_view.setCheckedItem(R.id.disciplinas_select)
        toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(applicationContext, R.color.colorSippa)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        title = "Disciplinas"
        val database = Room.databaseBuilder(
            applicationContext,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
        dialogPassword(database)
        Thread {
            val studentName = database.StudentDao().getStudent().name
            val studentMatricula = database.StudentDao().getStudent().matricula
            runOnUiThread {
                student_name_text.text = studentName
                student_matricula_text.text = studentMatricula
            }
        }.start()
        var fm = supportFragmentManager
        fm.beginTransaction().add(R.id.view_disciplinas, disciplinasFragment).commit()
        fm.beginTransaction().add(R.id.view_disciplinas, horasFragment).hide(horasFragment).commit()
        fm.beginTransaction().add(R.id.view_disciplinas, infoFragment).hide(infoFragment).commit()
        selectedFragment = disciplinasFragment
    }

    fun dialogPassword(database: StudentsDatabase){
        Thread {
            if(database.StudentDao().getStudent().login == ""){
                var student = database.StudentDao().getStudent()
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
                            database.StudentDao().delete()
                            database.StudentDao().insert(student)
                        }.start()
                    }
                    dialog.setNegativeButton("Mais Tarde") { dialog, which ->
                    }
                    var alert = dialog.create()
                    alert.show()
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.disciplinas_select -> {
                title = "Disciplinas"
                toolbar.setTitleTextColor(ContextCompat.getColor(applicationContext, R.color.colorSippa))
//                nav_view.itemTextColor = getColorStateList()
                window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.colorSippa)
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
