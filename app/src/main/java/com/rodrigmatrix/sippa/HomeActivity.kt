package com.rodrigmatrix.sippa

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentTransaction
import androidx.room.Room
import com.rodrigmatrix.sippa.persistance.Student
import com.rodrigmatrix.sippa.persistance.StudentsDatabase


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, HorasFragment.OnFragmentInteractionListener, DisciplinasFragment.OnFragmentInteractionListener {

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
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.view_disciplinas, DisciplinasFragment.newInstance())
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
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
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.disciplinas_select -> {
                title = "Disciplinas"
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.view_disciplinas, DisciplinasFragment())
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
            R.id.horas_select -> {
                title = "Horas Complementares"
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.view_disciplinas, HorasFragment())
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
            R.id.info_select -> {
                title = "Sobre"
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.view_disciplinas, InfoFragment())
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
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
