package com.rodrigmatrix.sippa

import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.rodrigmatrix.sippa.Serializer.*
import com.rodrigmatrix.sippa.persistance.StudentsDatabase
import kotlinx.android.synthetic.main.content_home.*

class Home : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.elevation = 40.0F
            actionBar.setHomeButtonEnabled(false)
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
        val serializer = Serializer()
        val database = Room.databaseBuilder(
            applicationContext,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
//        var thread = Thread {
//            var res = serializer.parseClasses(database.StudentDao().getStudent().responseHtml, database)
//            println(res)
//        }.start()
        var count = 0
        var grades: MutableList<Grade> = mutableListOf()
        var newsList: MutableList<News> = mutableListOf()
        var classPlan: MutableList<ClassPlan> = mutableListOf()
        var filesList: MutableList<File> = mutableListOf()
        recyclerView_disciplinas.layoutManager = LinearLayoutManager(this)
        val classes = listOf<Class>(com.rodrigmatrix.sippa.Serializer.Class("123", "Qualidade de Software", "Carla Illane Moreira Bezerra","",
            "","",grades, newsList, classPlan, filesList, "76,5", Attendance(26, 8)
        )
            , com.rodrigmatrix.sippa.Serializer.Class("123", "Introdução ao Desenvolvimento de Jogos", "Paulynne Matthews Jucá","",
                "","",grades, newsList, classPlan, filesList, "83,5", Attendance(26, 4)),
            com.rodrigmatrix.sippa.Serializer.Class("123", "Estrutura de Dados", "David Sena","",
                "","",grades, newsList, classPlan, filesList, "92,5", Attendance(26, 2)),
            com.rodrigmatrix.sippa.Serializer.Class("123", "Introdução à Computação e a Engenharia de Software", "Diana Braga","",
                "","",grades, newsList, classPlan, filesList, "70", Attendance(26, 12)),
            com.rodrigmatrix.sippa.Serializer.Class("123", "Cálculo Diferencial e Integral I", "Raphaella Hermont","",
                "","",grades, newsList, classPlan, filesList, "76,5", Attendance(26, 4)),
        com.rodrigmatrix.sippa.Serializer.Class("123", "Introdução ao Desenvolvimento Mobile", "Márcio Dantas","",
            "","",grades, newsList, classPlan, filesList, "76,5", Attendance(26, 0)))

        recyclerView_disciplinas.adapter = DisciplinasAdapter(classes)
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {

            }
            R.id.nav_gallery -> {

            }
            R.id.nav_share -> {

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
