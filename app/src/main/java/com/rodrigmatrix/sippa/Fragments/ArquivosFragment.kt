package com.rodrigmatrix.sippa


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import com.rodrigmatrix.sippa.persistance.StudentsDatabase
import com.rodrigmatrix.sippa.serializer.Serializer
import kotlinx.android.synthetic.main.fragment_arquivos.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.support.v4.runOnUiThread
import kotlin.coroutines.CoroutineContext


class ArquivosFragment : Fragment(), CoroutineScope {
    var id = ""
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.IO + job
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        swiperefresh_arquivos.setColorSchemeResources(R.color.colorPrimary)
        val database = Room.databaseBuilder(
            view.context,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        val jsession = database.studentDao().getStudent().jsession
        swiperefresh_arquivos?.isRefreshing = true
        swiperefresh_arquivos?.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(view.context, R.color.colorSwipeRefresh))
        launch(handler) {
            setClass(id, jsession)
        }
        swiperefresh_arquivos?.setOnRefreshListener {
            swiperefresh_arquivos?.isRefreshing = true
            launch(handler) {
                setClass(id, jsession)
            }
        }
    }
    override fun onStop() {
        job.cancel()
        coroutineContext.cancel()
        super.onStop()
    }
    override fun onDestroy() {
        job.cancel()
        coroutineContext.cancel()
        super.onDestroy()
    }
    private val handler = CoroutineExceptionHandler { _, throwable ->
        Log.e("Exception", ":$throwable")
    }

    private suspend fun setClass(id: String, jsession: String){
            if(!isConnected()) {
                return
            }
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("""https://academico.quixada.ufc.br/ServletCentral?comando=CmdListarFrequenciaTurmaAluno&id=$id""")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", jsession)
                .build()
        withContext(Dispatchers.IO){
            val response = client.newCall(request).execute()
            when {
                !response.isSuccessful -> showErrorConnection()
                else -> launch(handler) {
                    getFiles(jsession)
                }
            }
        }
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

    private suspend fun getFiles(jsession: String){
        if(!isConnected()){return}
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://academico.quixada.ufc.br/sippa/aluno_visualizar_arquivos.jsp?sorter=1")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Cookie", jsession)
            .build()
        withContext(Dispatchers.IO){
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                runOnUiThread {
                    showErrorConnection()
                }
            }
            else{
                val res = response.body()!!.string()
                val serializer = Serializer()
                val files = serializer.parseFiles(id,res)
                runOnUiThread {
                    swiperefresh_arquivos.isRefreshing = false
                    recyclerView_arquivos.layoutManager = LinearLayoutManager(context)
                    recyclerView_arquivos.adapter = ArquivosAdapter(files)
                }

            }
        }

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
