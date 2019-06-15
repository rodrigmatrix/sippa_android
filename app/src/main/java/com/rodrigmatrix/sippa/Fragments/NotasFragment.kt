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
import kotlinx.android.synthetic.main.fragment_horas.*
import kotlinx.android.synthetic.main.fragment_notas.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.support.v4.runOnUiThread
import kotlin.coroutines.CoroutineContext


class NotasFragment : Fragment(), CoroutineScope {
    var id = ""
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.IO + job
    lateinit var database: StudentsDatabase
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        swiperefresh_notas.setColorSchemeResources(R.color.colorPrimary)
        database = Room.databaseBuilder(
            view.context,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        setNotas()
        swiperefresh_notas?.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(view.context, R.color.colorSwipeRefresh))
        swiperefresh_notas!!.setOnRefreshListener {
            setNotas()
        }
    }
    private fun setNotas(){
        val jsession = database.studentDao().getStudent().jsession
        swiperefresh_notas!!.isRefreshing = true
        launch(handler) {
            setClass(id, jsession)
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

    private suspend fun setClass(id: String, jsession: String){
        if(!isConnected()){return}
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("""https://sistemas.quixada.ufc.br/apps/ServletCentral?comando=CmdListarFrequenciaTurmaAluno&id=$id""")
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
                getGrades(jsession)
            }
        }
    }

    private suspend fun getGrades(jsession: String){
            val client = OkHttpClient()
            val serializer = Serializer()
            val request = Request.Builder()
                .url("https://sistemas.quixada.ufc.br/apps/ServletCentral?comando=CmdVisualizarAvaliacoesAluno")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", jsession)
                .build()
        withContext(Dispatchers.IO){
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val res = response.body()!!.string()
                var grades = serializer.parseGrades(id, res)
                runOnUiThread {
                    swiperefresh_notas.isRefreshing = false
                    recyclerView_notas.layoutManager = LinearLayoutManager(context)
                    recyclerView_notas.adapter = NotasAdapter(grades)
                }
            }
            else{
                runOnUiThread {
                    showErrorConnection()
                }
            }
        }

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