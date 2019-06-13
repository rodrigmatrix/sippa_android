package com.rodrigmatrix.sippa

import android.content.Context
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
import kotlinx.android.synthetic.main.fragment_disciplinas.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.support.v4.runOnUiThread
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class DisciplinasFragment : Fragment(), CoroutineScope {

    private var listener: OnFragmentInteractionListener? = null
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.IO + job
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        swiperefresh!!.setColorSchemeResources(R.color.colorPrimary)
        val database = Room.databaseBuilder(
            view.context,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()

        swiperefresh!!.setOnRefreshListener {
            val jsession = database.studentDao().getStudent().jsession
            launch(handler) {
                setClasses(jsession, database)
            }
        }
        val jsession = database.studentDao().getStudent().jsession
        swiperefresh.isRefreshing = true
        launch(handler){
            setClasses(jsession, database)
        }
    }
    override fun onStop() {
        job.cancel()
        swiperefresh?.isRefreshing = false
        coroutineContext.cancel()
        super.onStop()
    }
    override fun onDestroy() {
        job.cancel()
        swiperefresh?.isRefreshing = false
        coroutineContext.cancel()
        super.onDestroy()
    }
    private val handler = CoroutineExceptionHandler { _, throwable ->
        Log.e("Exception", ":$throwable")
    }

    private suspend fun setClasses(jsession: String, database: StudentsDatabase){
            val cd = ConnectionDetector()
            val serializer = Serializer()
            val classes = serializer.parseClasses(database.studentDao().getStudent().responseHtml)
            val client = OkHttpClient()
            var parsed = true
            if(!cd.isConnectingToInternet(view!!.context)){
                runOnUiThread {
                    val snackbar = Snackbar.make(view!!, "Verifique sua conexão com a internet", Snackbar.LENGTH_LONG)
                    snackbar.show()
                    swiperefresh.isRefreshing = false
                }
                return
            }
            for (it in classes) {
                val request = Request.Builder()
                    .url("""https://sistemas.quixada.ufc.br/apps/ServletCentral?comando=CmdListarFrequenciaTurmaAluno&id=${it.id}""")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Cookie", jsession)
                    .build()
                withContext(Dispatchers.IO) {
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        val res = response.body()!!.string()
                        it.attendance = serializer.parseAttendance(res)
                        it.professorEmail = serializer.parseProfessorEmail(res)
                        var credits = serializer.parseClassPlan(res)
                        it.credits = credits.size * 2
                    }
                    else {
                        parsed = false
                        runOnUiThread {
                            swiperefresh.isRefreshing = false
                            val snackbar =
                                Snackbar.make(view!!, "Verifique sua conexão com a internet", Snackbar.LENGTH_LONG)
                            snackbar.show()
                        }
                    }
                }
            }
            if(parsed){
                try {
                    if(classes.size != 0){
                        runOnUiThread {
                            recyclerView_disciplinas.layoutManager = LinearLayoutManager(context)
                            recyclerView_disciplinas.adapter = DisciplinasAdapter(classes)
                            swiperefresh.isRefreshing = false
                        }
                    }
                    else{
                        runOnUiThread {
                            swiperefresh.isRefreshing = false
                            Snackbar.make(view!!, "Nenhuma disciplinas cadastrada em sua conta", Snackbar.LENGTH_LONG).show()
                        }
                    }
                }catch (e: Exception){
                    runOnUiThread {
                        swiperefresh.isRefreshing = false
                        Snackbar.make(view!!, "Erro ao exibir disciplinas. Tente novamente", Snackbar.LENGTH_LONG).show()
                    }
                    println(e)
                }
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_disciplinas, container, false)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            DisciplinasFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}