package com.rodrigmatrix.sippa


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class PlanoAulaFragment : Fragment() {
    var id = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        println("abriu plano aula")
//        swiperefresh_horas.setColorSchemeResources(R.color.colorPrimary)
//        val database = Room.databaseBuilder(
//            view.context,
//            StudentsDatabase::class.java, "database.db")
//            .fallbackToDestructiveMigration()
//            .build()
//        Thread {
//            val jsession = database.StudentDao().getStudent().jsession
//            runOnUiThread {
//                swiperefresh_horas!!.isRefreshing = true
//            }
//            setClass(id, jsession)
//        }.start()
//        swiperefresh_horas!!.setOnRefreshListener {
//
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plano_aula, container, false)
    }


    companion object {
        @JvmStatic
        fun newInstance(idDisciplina: String) =
            PlanoAulaFragment().apply {
                id = idDisciplina
            }
    }
}
