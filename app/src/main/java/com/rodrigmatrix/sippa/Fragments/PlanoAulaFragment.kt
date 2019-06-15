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
import kotlinx.android.synthetic.main.fragment_plano_aula.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.support.v4.runOnUiThread
import kotlin.coroutines.CoroutineContext


class PlanoAulaFragment : Fragment(), CoroutineScope {
    var id = ""
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.IO + job
    lateinit var database: StudentsDatabase
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        swiperefresh_plano.setColorSchemeResources(R.color.colorPrimary)
        database = Room.databaseBuilder(
            view.context,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        var jsession = database.studentDao().getStudent().jsession
        launch(handler) {
            setPlano(jsession)
        }
        swiperefresh_plano?.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(view.context, R.color.colorSwipeRefresh))
        swiperefresh_plano!!.setOnRefreshListener {
            setPlano(jsession)
        }
    }
    private fun setPlano(jsession: String){
        if(jsession == "offline"){
            var plan = database.studentDao().getClassPlan(id)
            runOnUiThread {
                var lastUpdate = database.studentDao().getStudent().lastUpdate
                Snackbar.make(view!!, "Modo offline. Última atualização de dados: $lastUpdate", Snackbar.LENGTH_LONG).show()
                swiperefresh_plano.isRefreshing = false
                recyclerView_plano.layoutManager = LinearLayoutManager(context)
                recyclerView_plano.adapter = PlanoAdapter(plan)
            }
        }
        else{
            swiperefresh_plano!!.isRefreshing = true
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
    private fun isConnected(): Boolean{
        val cd = ConnectionDetector()
        if(!cd.isConnectingToInternet(view!!.context)){
            runOnUiThread {
                val snackbar = Snackbar.make(view!!, "Verifique sua conexão com a internet", Snackbar.LENGTH_LONG)
                snackbar.show()
                swiperefresh_plano.isRefreshing = false
            }
            return false
        }
        return true
    }
    private fun showErrorConnection(){
        runOnUiThread {
            swiperefresh_plano!!.isRefreshing = false
            val snackbar = Snackbar.make(view!!, "Verifique sua conexão com a internet", Snackbar.LENGTH_LONG)
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
                val res = response.body()!!.string()
                val serializer = Serializer()
                var plan = serializer.parseClassPlan(id, res)
                runOnUiThread {
                    swiperefresh_plano.isRefreshing = false
                    recyclerView_plano.layoutManager = LinearLayoutManager(context)
                    recyclerView_plano.adapter = PlanoAdapter(plan)
                }
            }
        }
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
