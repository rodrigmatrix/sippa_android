package com.rodrigmatrix.sippa

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import com.rodrigmatrix.sippa.Serializer.Serializer
import com.rodrigmatrix.sippa.persistance.StudentsDatabase
import kotlinx.android.synthetic.main.fragment_horas.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.support.v4.runOnUiThread
import java.lang.Exception

class HorasFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        swiperefresh_horas!!.setColorSchemeResources(R.color.colorPrimary)
        val database = Room.databaseBuilder(
            view.context,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
        Thread {
            val jsession = database.StudentDao().getStudent().jsession
            runOnUiThread {
                swiperefresh_horas!!.isRefreshing = true
            }
            setHoras(jsession)
        }.start()
        swiperefresh_horas!!.setOnRefreshListener {
            Thread{
                val jsession = database.StudentDao().getStudent().jsession
                setHoras(jsession)
            }.start()
        }
    }

    private fun setHoras(jsession: String){
        Thread {
            val cd = ConnectionDetector()
            val serializer = Serializer()
            val client = OkHttpClient()
            if(!cd.isConnectingToInternet(view!!.context)){
                runOnUiThread {
                    val snackbar = Snackbar.make(view!!, "Verifique sua conexão com a internet", Snackbar.LENGTH_LONG)
                    snackbar.show()
                    swiperefresh_horas.isRefreshing = false
                }
                return@Thread
            }
            val request = Request.Builder()
                .url("https://sistemas.quixada.ufc.br/apps/ServletCentral?comando=CmdLoginSisacAluno")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", jsession)
                .build()
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val res = response.body()!!.string()
                    var horas = serializer.parseHorasComplementares(res)
                    runOnUiThread {
                        try {
                            recyclerView_horas.layoutManager = LinearLayoutManager(context)
                            recyclerView_horas.adapter = HorasAdapter(horas)
                            swiperefresh_horas.isRefreshing = false
                        }catch (e: Exception){
                            runOnUiThread {
                                swiperefresh_horas.isRefreshing = false
                                val snackbar = Snackbar.make(view!!, "Erro ao exibir dados. Tente novamente", Snackbar.LENGTH_LONG)
                                snackbar.show()
                            }
                            println(e)
                        }
                    }
                }
                else{
                    runOnUiThread {
                        swiperefresh_horas.isRefreshing = false
                        val snackbar = Snackbar.make(view!!, "Verifique sua conexão com a internet", Snackbar.LENGTH_LONG)
                        snackbar.show()
                    }
                }
            }catch(e: Exception){
                println(e)
            }
        }.start()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_horas, container, false)
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HorasFragment().apply {
            }
    }
}
