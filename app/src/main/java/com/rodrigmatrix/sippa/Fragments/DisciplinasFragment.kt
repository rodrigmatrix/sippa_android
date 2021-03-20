package com.rodrigmatrix.sippa.fragments

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
import com.google.android.gms.ads.*
import com.google.android.material.snackbar.Snackbar
import com.rodrigmatrix.sippa.BuildConfig
import com.rodrigmatrix.sippa.ConnectionDetector
import com.rodrigmatrix.sippa.DisciplinasAdapter
import com.rodrigmatrix.sippa.R
import com.rodrigmatrix.sippa.persistance.Class
import com.rodrigmatrix.sippa.persistence.StudentsDatabase
import com.rodrigmatrix.sippa.serializer.Serializer
import kotlinx.android.synthetic.main.fragment_disciplinas.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.support.v4.runOnUiThread
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeoutException
import kotlin.coroutines.CoroutineContext

class DisciplinasFragment : Fragment(R.layout.fragment_disciplinas), CoroutineScope {


    override val coroutineContext: CoroutineContext get() = Dispatchers.IO
    private lateinit var database: StudentsDatabase

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        swiperefresh!!.setColorSchemeResources(R.color.colorPrimary)
        database = StudentsDatabase.invoke(context!!)
        swiperefresh?.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(view.context,
            R.color.colorSwipeRefresh
        ))
        val jsession = database.studentDao().getStudent().jsession
        swiperefresh?.isRefreshing = false
        swiperefresh?.setOnRefreshListener {
            try {
                launch(handler){
                    setClasses(jsession, database)
                }
            }catch(e: Exception){
                runOnUiThread {
                    swiperefresh?.isRefreshing = false
                    Snackbar.make(view, e.toString(), Snackbar.LENGTH_LONG).show()
                }
            }
        }
        try {
            launch(handler){
                setClasses(jsession, database)
            }
        }catch(e: Exception){
            runOnUiThread {
                swiperefresh?.isRefreshing = false
                Snackbar.make(view, e.toString(), Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private val handler = CoroutineExceptionHandler { _, throwable ->
        runOnUiThread {
            swiperefresh?.isRefreshing = false
            Snackbar.make(fragment_disciplinas, "${throwable.message}", Snackbar.LENGTH_INDEFINITE).show()
        }
        Log.e("Exception", ":${throwable.message}")
    }

    override fun onStop() {
        coroutineContext.cancel()
        swiperefresh?.isRefreshing = false
        super.onStop()
    }
    override fun onDestroy() {
        coroutineContext.cancel()
        swiperefresh?.isRefreshing = false
        super.onDestroy()
    }

    private suspend fun setClasses(jsession: String, database: StudentsDatabase){
        swiperefresh?.isRefreshing = true
        if(jsession == "offline"){
            val classes = database.studentDao().getClasses()
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
                    Snackbar.make(view!!, "Nenhuma disciplina encontrada em sua conta", Snackbar.LENGTH_LONG).show()
                }
            }
        }
        else{
            val cd = ConnectionDetector()
            val serializer = Serializer()
            val classes = serializer.parseClasses(database.studentDao().getStudent().responseHtml)
            val client = OkHttpClient()
            var parsed = true
            database.studentDao().deleteClasses()
            database.studentDao().deleteHoras()
            database.studentDao().deleteGrades()
            database.studentDao().deleteClassPlan()
            database.studentDao().deleteFiles()
            database.studentDao().deleteNews()
            val context = context
            if(context != null){
                if(!cd.isConnectingToInternet(context)){
                    runOnUiThread {
                        Snackbar.make(view!!, "Verifique sua conexão com a internet", Snackbar.LENGTH_LONG)
                            .show()
                        swiperefresh.isRefreshing = false
                    }
                    return
                }
            }
            for (it in classes) {
                val request = Request.Builder()
                    .url("""https://academico.quixada.ufc.br/ServletCentral?comando=CmdListarFrequenciaTurmaAluno&id=${it.id}""")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Cookie", jsession)
                    .build()
                withContext(Dispatchers.IO) {
                    try {
                        val response = client.newCall(request).execute()
                        if (response.isSuccessful) {
                            val res = response.body()!!.string()
                            val attendance = serializer.parseAttendance(res)
                            it.totalAttendance = attendance.totalAttendance
                            it.missed = attendance.totalMissed
                            it.professorEmail = serializer.parseProfessorEmail(res)
                            val credits = serializer.parseClassPlan(it.id, res)
                            it.credits = credits.size * 2
                            val studentClass = Class(it.id, it.name, it.professorName, it.professorEmail, it.percentageAttendance, it.credits, it.missed, it.totalAttendance)
                            database.studentDao().insertClass(studentClass)
                            val news = serializer.parseNews(it.id, res)
                            val classPlan = serializer.parseClassPlan(it.id, res)
                            classPlan.forEach {
                                database.studentDao().insertClassPlan(it)
                            }
                            news.forEach {
                                database.studentDao().insertNews(it)
                            }
                            setGrades(it.id, jsession)
                        }
                        else{
                            parsed = false
                            runOnUiThread {
                                swiperefresh.isRefreshing = false
                                Snackbar.make(view!!, "Verifique sua conexão com a internet", Snackbar.LENGTH_LONG).show()
                            }
                        }
                    }catch(e: TimeoutException){
                        parsed = false
                        runOnUiThread {
                            swiperefresh.isRefreshing = false
                            Snackbar.make(view!!, "Tempo de conexao expirou", Snackbar.LENGTH_LONG).show()
                        }
                    }

                }
                if(!parsed){
                    return
                }
            }
            if(parsed){
                try {
                    if(classes.size != 0){
                        runOnUiThread {
                            val sdf = SimpleDateFormat("dd/MM/yy hh:mm")
                            val currentDate = sdf.format(Date())
                            val student = database.studentDao().getStudent()
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
                        val sdf = SimpleDateFormat("dd/MM/yy hh:mm")
                        val currentDate = sdf.format(Date())
                        val student = database.studentDao().getStudent()
                        student.lastUpdate = currentDate
                        student.hasSavedData = true
                        database.studentDao().deleteStudent()
                        database.studentDao().insertStudent(student)
                        swiperefresh.isRefreshing = false
                        runOnUiThread {
                            swiperefresh.isRefreshing = false
                            Snackbar.make(view!!, "Nenhuma disciplina cadastrada em sua conta", Snackbar.LENGTH_LONG).show()
                        }
                        return
                    }
                }catch (e: Exception){
                    runOnUiThread {
                        swiperefresh.isRefreshing = false
                        Snackbar.make(view!!, "Erro ao exibir disciplinas. Tente novamente", Snackbar.LENGTH_LONG).show()
                    }
                    return
                }
            }
        }
    }

    private suspend fun setGrades(id: String, jsession: String){
        val request = Request.Builder()
            .url("https://academico.quixada.ufc.br/ServletCentral?comando=CmdVisualizarAvaliacoesAluno")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Cookie", jsession)
            .build()
        try {
            val client = OkHttpClient()
            withContext(Dispatchers.IO) {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val serializer = Serializer()
                    val grades = serializer.parseGrades(id, response.body()!!.string())
                    grades.forEach {
                        database.studentDao().insertGrade(it)
                    }
                }
            }
        }catch(e: TimeoutException){
            runOnUiThread {
                swiperefresh.isRefreshing = false
                Snackbar.make(view!!, "Tempo de conexão expirou", Snackbar.LENGTH_LONG).show()
            }
        }

    }


}