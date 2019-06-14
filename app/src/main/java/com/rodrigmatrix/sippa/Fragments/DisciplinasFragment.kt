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
import com.rodrigmatrix.sippa.persistance.Class
import com.rodrigmatrix.sippa.persistance.StudentsDatabase
import com.rodrigmatrix.sippa.serializer.Serializer
import kotlinx.android.synthetic.main.fragment_arquivos.*
import kotlinx.android.synthetic.main.fragment_disciplinas.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.support.v4.runOnUiThread
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class DisciplinasFragment : Fragment(), CoroutineScope {

    private var listener: OnFragmentInteractionListener? = null
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.IO + job
    private var loginType = ""
    private lateinit var database: StudentsDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        swiperefresh!!.setColorSchemeResources(R.color.colorPrimary)
        database = Room.databaseBuilder(
            view.context,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        swiperefresh?.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(view.context, R.color.colorSwipeRefresh))
        var jsession = database.studentDao().getStudent().jsession
        if(loginType == "offline"){
            jsession = "offline"
        }
        swiperefresh?.setOnRefreshListener {
            launch(handler){
                setClasses(jsession, database)
            }
        }
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
        runOnUiThread {
            swiperefresh?.isRefreshing = false
            Snackbar.make(view!!, "Erro ao exibir disciplinas. Por favor, me envie um email(email na tela sobre)", Snackbar.LENGTH_LONG).show()
        }
        job.cancel()
        coroutineContext.cancel()
        Log.e("Exception", ":$throwable")
    }

    private suspend fun setClasses(jsession: String, database: StudentsDatabase){
        if(jsession == "offline"){
            var lastUpdate = database.studentDao().getStudent().lastUpdate
            var classes = database.studentDao().getClasses()
            runOnUiThread {
                recyclerView_disciplinas.layoutManager = LinearLayoutManager(context)
                recyclerView_disciplinas.adapter = DisciplinasAdapter(classes)
                swiperefresh.isRefreshing = false
                Snackbar.make(view!!, "Modo offline. Última atualização de dados: $lastUpdate", Snackbar.LENGTH_LONG).show()
            }
            return
        }
        else{
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
                        var attendance = serializer.parseAttendance(res)
                        it.totalAttendance = attendance.totalAttendance
                        it.missed = attendance.totalMissed
                        it.professorEmail = serializer.parseProfessorEmail(res)
                        var credits = serializer.parseClassPlan(res)
                        it.credits = credits.size * 2
                        var studentClass = Class(it.id, it.name, it.professorName, it.professorEmail, it.percentageAttendance, it.credits, it.missed, it.totalAttendance)
                        database.studentDao().insertClass(studentClass)
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
                            val sdf = SimpleDateFormat("dd/MM/yy hh:mm")
                            val currentDate = sdf.format(Date())
                            var student = database.studentDao().getStudent()
                            student.lastUpdate = currentDate
                            student.hasSavedData = true
                            database.studentDao().deleteStudent()
                            database.studentDao().insertStudent(student)
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
                    println("exce $e")
                }
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
        fun newInstance(lg: String) =
            DisciplinasFragment().apply {
                arguments = Bundle().apply {
                    loginType = lg
                }
            }
    }
}