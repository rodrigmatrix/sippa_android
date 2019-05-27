package com.rodrigmatrix.sippa

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import com.rodrigmatrix.sippa.persistance.Student
import com.rodrigmatrix.sippa.persistance.StudentsDatabase
import kotlinx.android.synthetic.main.nav_header_home.*
import org.jetbrains.anko.textColor


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, HorasFragment.OnFragmentInteractionListener, DisciplinasFragment.OnFragmentInteractionListener {
    private var doubleBackToExitPressedOnce = false
    private var selectedFragment = Fragment()
    private var disciplinasFragment = DisciplinasFragment()
    private var horasFragment = HorasFragment()
    private var infoFragment = InfoFragment()
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        this.onBackPressed()
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val headerView = navView.getHeaderView(0)
        navView.setCheckedItem(R.id.disciplinas_select)
        val nameText: TextView = headerView.findViewById(R.id.student_name_text)
        val matriculaText: TextView = headerView.findViewById(R.id.student_matricula_text)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        toggle.drawerArrowDrawable.color = getColor(R.color.colorAccent)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
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
                nameText.text = studentName
                matriculaText.text = studentMatricula
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
                sistema_name.text = "Sippa"
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
                sistema_name.text = "Sisac"
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
                sistema_name.text = "Sippa"
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
