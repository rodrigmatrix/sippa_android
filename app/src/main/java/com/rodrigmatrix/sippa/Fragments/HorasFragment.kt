package com.rodrigmatrix.sippa

import android.net.Uri
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
import kotlinx.android.synthetic.main.fragment_horas.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.support.v4.runOnUiThread
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class HorasFragment : Fragment(), CoroutineScope {
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.IO + job
    private var loginType = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        swiperefresh_horas!!.setColorSchemeResources(R.color.colorSisac)
        val database = Room.databaseBuilder(
            view.context,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        var student = database.studentDao().getStudent()
        val jsession = student.jsession
        if(loginType == "offline"){
            Snackbar.make(view, "Você está no modo offline. A última atualização de dados foi em ${student.lastUpdate}", Snackbar.LENGTH_LONG).show()
        }
        swiperefresh_horas?.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(view.context, R.color.colorSwipeRefresh))
        swiperefresh_horas?.isRefreshing = true
        launch(handler){
            setHoras(jsession)
        }
        swiperefresh_horas?.setOnRefreshListener {
            launch(handler){
                setHoras(jsession)
            }
        }
    }
    override fun onStop() {
        swiperefresh_horas?.isRefreshing = false
        job.cancel()
        coroutineContext.cancel()
        super.onStop()
    }
    override fun onDestroy() {
        swiperefresh_horas?.isRefreshing = false
        job.cancel()
        coroutineContext.cancel()
        super.onDestroy()
    }
    private val handler = CoroutineExceptionHandler { _, throwable ->
        Log.e("Exception", ":$throwable")
    }

    private suspend fun setHoras(jsession: String){
        val cd = ConnectionDetector()
        val serializer = Serializer()
        val client = OkHttpClient()
        if(!cd.isConnectingToInternet(view!!.context)){
            runOnUiThread {
                Snackbar.make(view!!, "Verifique sua conexão com a internet", Snackbar.LENGTH_LONG).show()
                swiperefresh_horas.isRefreshing = false
            }
            return
        }
        val request = Request.Builder()
            .url("https://sistemas.quixada.ufc.br/apps/ServletCentral?comando=CmdLoginSisacAluno")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Cookie", jsession)
            .build()
        withContext(Dispatchers.IO){
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val res = response.body()!!.string()
                var horas = serializer.parseHorasComplementares(res)
                runOnUiThread {
                    recyclerView_horas.layoutManager = LinearLayoutManager(context)
                    recyclerView_horas.adapter = HorasAdapter(horas)
                    swiperefresh_horas.isRefreshing = false
                }
            }
            else{
                runOnUiThread {
                    swiperefresh_horas.isRefreshing = false
                    val snackbar = Snackbar.make(view!!, "Verifique sua conexão com a internet", Snackbar.LENGTH_LONG)
                    snackbar.show()
                }
            }
        }
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
        fun newInstance(lg: String) =
            HorasFragment().apply {
                loginType = lg
            }
    }
}
