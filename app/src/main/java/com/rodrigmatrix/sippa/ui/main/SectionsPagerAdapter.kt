package com.rodrigmatrix.sippa.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.rodrigmatrix.sippa.*
import java.util.ArrayList

private val fragmentList = listOf( NoticiasFragment(), NotasFragment(), PlanoAulaFragment(), ArquivosFragment())
private val titleList = listOf( "Avisos", "Notas", "Aulas", "Arquivos")

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titleList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }
}