package com.rodrigmatrix.sippa

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
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.rodrigmatrix.sippa.Serializer.*
import com.rodrigmatrix.sippa.persistance.StudentsDatabase
import kotlinx.android.synthetic.main.content_home.*
import okhttp3.*
import java.lang.Exception


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, HorasFragment.OnFragmentInteractionListener, DisciplinasFragment.OnFragmentInteractionListener {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
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
        //Atenção: Seu tempo de conexão expirou.

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


    override fun onBackPressed(){
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
            R.id.disciplinas_select -> {
                title = "Disciplinas"

                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.view_disciplinas, DisciplinasFragment.newInstance())
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
            R.id.horas_select -> {
                title = "Horas Complementares"
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.view_disciplinas, HorasFragment.newInstance())
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
            R.id.info_select -> {
                title = "Sobre"
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.view_disciplinas, InfoFragment.newInstance())
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
