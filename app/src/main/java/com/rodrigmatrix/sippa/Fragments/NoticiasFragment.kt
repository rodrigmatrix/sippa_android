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
import kotlinx.android.synthetic.main.fragment_noticias.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.support.v4.runOnUiThread
import kotlin.coroutines.CoroutineContext


class NoticiasFragment : Fragment(), CoroutineScope {
    var id = ""
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.IO + job
    lateinit var database: StudentsDatabase
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        swiperefresh_noticias.setColorSchemeResources(R.color.colorPrimary)
        database = Room.databaseBuilder(
            view.context,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        var jsession = database.studentDao().getStudent().jsession
        setNoticias(jsession)
        swiperefresh_noticias?.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(view.context, R.color.colorSwipeRefresh))
        swiperefresh_noticias?.setOnRefreshListener {
            setNoticias(jsession)
        }
    }
    private fun setNoticias(jsession: String){
        swiperefresh_noticias?.isRefreshing = true
        if(jsession == "offline"){
            var news = database.studentDao().getNews(id)
            runOnUiThread {
                var lastUpdate = database.studentDao().getStudent().lastUpdate
                Snackbar.make(view!!, "Modo offline. Última atualização de dados: $lastUpdate", Snackbar.LENGTH_LONG).show()
                swiperefresh_noticias.isRefreshing = false
                recyclerView_noticias.layoutManager = LinearLayoutManager(context)
                recyclerView_noticias.adapter = NoticiasAdapter(news)
            }
        }
        else{
            launch(handler) {
                setClass(id, jsession)
            }
        }
    }
    private val handler = CoroutineExceptionHandler { _, throwable ->
        Log.e("Exception", ":$throwable")
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
    private fun isConnected(): Boolean{
        val cd = ConnectionDetector()
        if(!cd.isConnectingToInternet(view!!.context)){
            runOnUiThread {
                val snackbar = Snackbar.make(view!!, "Verifique sua conexão com a internet", Snackbar.LENGTH_LONG)
                snackbar.show()
                swiperefresh_noticias.isRefreshing = false
            }
            return false
        }
        return true
    }
    private fun showErrorConnection(){
        runOnUiThread {
            swiperefresh_noticias.isRefreshing = false
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
                showErrorConnection()
            }
            else{
                val res = response.body()!!.string()
                val serializer = Serializer()
                var news = serializer.parseNews(id, res)
                println(news)
                runOnUiThread {
                    swiperefresh_noticias.isRefreshing = false
                    recyclerView_noticias.layoutManager = LinearLayoutManager(context)
                    recyclerView_noticias.adapter = NoticiasAdapter(news)
                }
            }
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_noticias, container, false)
    }


    companion object {
        @JvmStatic
        fun newInstance(idDisciplina: String) =
            NoticiasFragment().apply {
                arguments = Bundle().apply {
                    id = idDisciplina
                }
            }
    }
}
