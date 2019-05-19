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
        sectionsPagerAdapter.addFragment(InfoFragment(), "Detalhes")
        sectionsPagerAdapter.addFragment(InfoFragment(), "Detalhes")
//        sectionsPagerAdapter.addFragment(InfoFragment(), "Detalhes")
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp)
        toolbar.setNavigationOnClickListener {
            this.finish()
        }
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }

    override fun onBackPressed(){
        this.finish()
    }
}