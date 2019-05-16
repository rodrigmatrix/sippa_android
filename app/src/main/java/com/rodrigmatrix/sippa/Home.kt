package com.rodrigmatrix.sippa

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.kittinunf.fuel.Fuel
import com.google.android.material.snackbar.Snackbar
import com.rodrigmatrix.sippa.Serializer.*
import com.rodrigmatrix.sippa.persistance.Student
import com.rodrigmatrix.sippa.persistance.StudentsDatabase
import kotlinx.android.synthetic.main.activity_disciplina.view.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_home.*
import okhttp3.*
import java.io.IOException
import java.lang.Exception



class Home : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        this.onBackPressed()
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val reload = findViewById<SwipeRefreshLayout>(R.id.swiperefresh)
        val headerView = navView.getHeaderView(0)
        val nameText: TextView = headerView.findViewById(R.id.student_name_text)
        val matriculaText: TextView = headerView.findViewById(R.id.student_matricula_text)
        val view: View = findViewById(R.id.view_disciplinas)

        //actionBar.setHomeButtonEnabled(false)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
        title = "Disciplinas"

        reload.isRefreshing = true
        val database = Room.databaseBuilder(
            applicationContext,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
        setClasses(view, reload, nameText, matriculaText, database)
        reload.setOnRefreshListener {
            setClasses(view, reload, nameText, matriculaText, database)
        }

    }
    fun setClasses(view: View, reload: SwipeRefreshLayout, nameText:TextView, matriculaText: TextView, database: StudentsDatabase){
        Thread {
            val cd = ConnectionDetector()
            val jsession = database.StudentDao().getStudent().jsession
            val studentName = database.StudentDao().getStudent().name
            val studentMatricula = database.StudentDao().getStudent().matricula
            val serializer = Serializer()
            val classes = serializer.parseClasses(database.StudentDao().getStudent().responseHtml, database)
            val client = OkHttpClient()
            var parsed = true
            if(!cd.isConnectingToInternet(this)){
                runOnUiThread {
                    val snackbar = Snackbar.make(view, "Verifique sua conexão com a internet", Snackbar.LENGTH_LONG)
                    snackbar.show()
                    reload.isRefreshing = false
                }
                return@Thread
            }
            for (it in classes){
                val request = Request.Builder()
                    .url("""https://sistemas.quixada.ufc.br/apps/ServletCentral?comando=CmdListarFrequenciaTurmaAluno&id=${it.id}""")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Cookie", jsession)
                    .build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val res = response.body()!!.string()
                    it.attendance = serializer.parseAttendance(res)
                    it.news = serializer.parseNews(res)
                    it.professorEmail = serializer.parseProfessorEmail(res)
                }
                else{
                    parsed = false
                    val snackbar = Snackbar.make(view, "Verifique sua conexão com a internet ou se o sippa está funcionando no momento", Snackbar.LENGTH_LONG)
                    snackbar.show()
                }
            }
            if(parsed){
                runOnUiThread {
                    nameText.text = studentName
                    matriculaText.text = studentMatricula
                    recyclerView_disciplinas.layoutManager = LinearLayoutManager(this)
                    recyclerView_disciplinas.adapter = DisciplinasAdapter(classes)
                    reload.isRefreshing = false
                }
            }
        }.start()
    }

    override fun onBackPressed() {

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
