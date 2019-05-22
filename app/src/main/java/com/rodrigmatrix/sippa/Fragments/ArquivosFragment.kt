package com.rodrigmatrix.sippa


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import com.rodrigmatrix.sippa.persistance.StudentsDatabase
import com.rodrigmatrix.sippa.serializer.Serializer
import kotlinx.android.synthetic.main.fragment_arquivos.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.support.v4.runOnUiThread


class ArquivosFragment : Fragment() {
    var id = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        swiperefresh_arquivos.setColorSchemeResources(R.color.colorPrimary)
        val database = Room.databaseBuilder(
            view.context,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
        Thread {
            val jsession = database.StudentDao().getStudent().jsession
            runOnUiThread {
                swiperefresh_arquivos!!.isRefreshing = true
            }
            setClass(id, jsession)
        }.start()
        swiperefresh_arquivos!!.setOnRefreshListener {
            Thread {
                val jsession = database.StudentDao().getStudent().jsession
                runOnUiThread {
                    swiperefresh_arquivos!!.isRefreshing = true
                }
                setClass(id, jsession)
            }.start()
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
                    getFiles(jsession)
                }
            }
            catch(e: Exception){
                println("exception get set class: $e")
                showErrorConnection()
            }
        }.start()
    }

    private fun isConnected(): Boolean{
        val cd = ConnectionDetector()
        if(!cd.isConnectingToInternet(view!!.context)){
            runOnUiThread {
                val snackbar = Snackbar.make(view!!, "Verifique sua conexão com a internet", Snackbar.LENGTH_LONG)
                snackbar.show()
                swiperefresh_arquivos.isRefreshing = false
            }
            return false
        }
        return true
    }
    private fun showErrorConnection(){
        runOnUiThread {
            swiperefresh_arquivos.isRefreshing = false
            val snackbar =
                Snackbar.make(view!!, "Verifique sua conexão com a internet", Snackbar.LENGTH_LONG)
            snackbar.show()
        }
    }

    private fun getFiles(jsession: String){
        Thread {
            if(!isConnected()){return@Thread}
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://sistemas.quixada.ufc.br/apps/sippa/aluno_visualizar_arquivos.jsp?sorter=1")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", jsession)
                .build()
            try {
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    showErrorConnection()
                }
                else{
                    val res = response.body()!!.string()
                    val serializer = Serializer()
                    val files = serializer.parseFiles(res)
                    runOnUiThread {
                        swiperefresh_arquivos.isRefreshing = false
                        recyclerView_arquivos.layoutManager = LinearLayoutManager(context)
                        recyclerView_arquivos.adapter = ArquivosAdapter(files)
                    }

                }
            }
            catch(e: Exception){
                println("exception get files: $e")
                showErrorConnection()
            }
        }.start()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_arquivos, container, false)
    }


    companion object {
        @JvmStatic
        fun newInstance(idDisciplina: String) =
            ArquivosFragment().apply {
                arguments = Bundle().apply {
                    id = idDisciplina
                }
            }
    }
}
