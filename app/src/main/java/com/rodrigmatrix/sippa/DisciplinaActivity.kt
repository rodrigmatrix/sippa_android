package com.rodrigmatrix.sippa

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.rodrigmatrix.sippa.ui.main.SectionsPagerAdapter

class DisciplinaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disciplina)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        var id = intent.getStringExtra("id")
        println("id na activity $id")
        sectionsPagerAdapter.removeFragments()
        sectionsPagerAdapter.addFragment(NoticiasFragment.newInstance(id))
        sectionsPagerAdapter.addFragment(NotasFragment.newInstance(id))
        sectionsPagerAdapter.addFragment(PlanoAulaFragment.newInstance(id))
        sectionsPagerAdapter.addFragment(ArquivosFragment.newInstance(id))

        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.offscreenPageLimit = 4
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp)
        toolbar.title = intent.getStringExtra("name")
        toolbar.setNavigationOnClickListener {
            this.finish()
        }
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        viewPager.adapter = sectionsPagerAdapter
        tabs.getTabAt(0)!!.setIcon(R.drawable.ic_noticias_24dp)
        tabs.getTabAt(1)!!.setIcon(R.drawable.ic_notas_24dp)
        tabs.getTabAt(2)!!.setIcon(R.drawable.ic_plano_24dp)
        tabs.getTabAt(3)!!.setIcon(R.drawable.ic_arquivos_24dp)
        if(intent.getStringExtra("option") == "grades"){
            val op = tabs.getTabAt(1)
            op!!.select()
        }
        else{
            val op = tabs.getTabAt(0)
            op!!.select()
        }
    }

    override fun onBackPressed(){
        this.finish()
    }
}