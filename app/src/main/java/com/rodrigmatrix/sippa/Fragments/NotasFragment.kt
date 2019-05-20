package com.rodrigmatrix.sippa


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
import kotlinx.android.synthetic.main.fragment_notas.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.support.v4.runOnUiThread


class NotasFragment : Fragment() {
    var id = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        swiperefresh_notas.setColorSchemeResources(R.color.colorPrimary)
        val database = Room.databaseBuilder(
            view.context,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
        Thread {
            val jsession = database.StudentDao().getStudent().jsession
            runOnUiThread {
                swiperefresh_notas!!.isRefreshing = true
            }
            setClass(id, jsession)
        }.start()
        swiperefresh_notas!!.setOnRefreshListener {
            Thread {
                val jsession = database.StudentDao().getStudent().jsession
                runOnUiThread {
                    swiperefresh_notas!!.isRefreshing = true
                }
                setClass(id, jsession)
            }.start()
        }
    }
    private fun isConnected(): Boolean{
        val cd = ConnectionDetector()
        if(!cd.isConnectingToInternet(view!!.context)){
            runOnUiThread {
                val snackbar = Snackbar.make(view!!, "Verifique sua conexão com a internet", Snackbar.LENGTH_LONG)
                snackbar.show()
                swiperefresh_notas.isRefreshing = false
            }
            return false
        }
        return true
    }
    private fun showErrorConnection(){
        runOnUiThread {
            swiperefresh_notas.isRefreshing = false
            val snackbar =
                Snackbar.make(view!!, "Verifique sua conexão com a internet", Snackbar.LENGTH_LONG)
            snackbar.show()
        }
    }

    private fun setClass(id: String, jsession: String){
        Thread {
            if(!isConnected()){return@Thread}
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("""https://sistemas.quixada.ufc.br/apps/ServletCentral?comando=CmdListarFrequenciaTurmaAluno&id=$id""")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", jsession)
                .build()
                    try {
                        val response = client.newCall(request).execute()
                        if (!response.isSuccessful) {
                            showErrorConnection()
                        }
                        else{
                            getGrades(jsession)
                        }
                    }
                    catch(e: Exception){
                        showErrorConnection()
                    }
        }.start()
    }

    private fun getGrades(jsession: String){
        Thread {
            val cd = ConnectionDetector()
            val client = OkHttpClient()
            val serializer = Serializer()
            if(!isConnected()){return@Thread}
            val request = Request.Builder()
                .url("https://sistemas.quixada.ufc.br/apps/ServletCentral?comando=CmdVisualizarAvaliacoesAluno")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", jsession)
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val res = response.body()!!.string()
                var grades = serializer.parseGrades(res)
                runOnUiThread {
                    swiperefresh_notas.isRefreshing = false
                    recyclerView_notas.layoutManager = LinearLayoutManager(context)
                    recyclerView_notas.adapter = NotasAdapter(grades)
                }
            }
            else{
                showErrorConnection()
            }
        }.start()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notas, container, false)
    }


    companion object {
        @JvmStatic
        fun newInstance(idDisciplina: String) =
            NotasFragment().apply {
                id = idDisciplina
            }
    }
}


