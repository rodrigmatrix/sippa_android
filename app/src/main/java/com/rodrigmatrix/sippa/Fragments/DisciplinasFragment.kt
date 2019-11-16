package com.rodrigmatrix.sippa.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.android.gms.ads.*
import com.google.android.material.snackbar.Snackbar
import com.rodrigmatrix.sippa.BuildConfig
import com.rodrigmatrix.sippa.ConnectionDetector
import com.rodrigmatrix.sippa.DisciplinasAdapter
import com.rodrigmatrix.sippa.R
import com.rodrigmatrix.sippa.persistance.Class
import com.rodrigmatrix.sippa.persistance.StudentsDatabase
import com.rodrigmatrix.sippa.serializer.Serializer
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
    private lateinit var database: StudentsDatabase
    private lateinit var mInterstitialAd: InterstitialAd
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
        swiperefresh?.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(view.context,
            R.color.colorSwipeRefresh
        ))
        var jsession = database.studentDao().getStudent().jsession
        swiperefresh?.isRefreshing = false
        loadAd()
        swiperefresh?.setOnRefreshListener {
            try {
                launch{
                    setClasses(jsession, database)
                }
            }catch(e: Exception){
                runOnUiThread {
                    swiperefresh?.isRefreshing = false
                    Snackbar.make(view, e.toString(), Snackbar.LENGTH_LONG).show()
                }
                job.cancel()
            }
        }
        try {
            launch{
                setClasses(jsession, database)
            }
        }catch(e: Exception){
            runOnUiThread {
                swiperefresh?.isRefreshing = false
                Snackbar.make(view, e.toString(), Snackbar.LENGTH_LONG).show()
            }
            job.cancel()
        }

    }

    private fun loadAd(){
        MobileAds.initialize(context){}
        var adUnitInterstitial = getString(R.string.ad_unit_interstitial)
        val adRequest = AdRequest.Builder()
        if(BuildConfig.DEBUG){
            adRequest.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
            adUnitInterstitial = "ca-app-pub-3940256099942544/1033173712"
        }
        mInterstitialAd = InterstitialAd(context)
        mInterstitialAd.adUnitId = adUnitInterstitial
        mInterstitialAd.loadAd(adRequest.build())
        mInterstitialAd.adListener = object: AdListener() {
            override fun onAdLoaded() {
                mInterstitialAd.show()
            }
        }
        val adRequestBanner = AdRequest.Builder()
        if(BuildConfig.DEBUG){
            adRequestBanner.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        }
        adView?.loadAd(adRequestBanner.build())
    }

    override fun onStop() {
        job.cancel()
        coroutineContext.cancel()
        swiperefresh?.isRefreshing = false
        super.onStop()
    }
    override fun onDestroy() {
        job.cancel()
        coroutineContext.cancel()
        swiperefresh?.isRefreshing = false
        super.onDestroy()
    }

    private suspend fun setClasses(jsession: String, database: StudentsDatabase){
        swiperefresh?.isRefreshing = true
        if(jsession == "offline"){
            val classes = database.studentDao().getClasses()
            if(classes.size != 0){
                recyclerView_disciplinas.layoutManager = LinearLayoutManager(context)
                recyclerView_disciplinas.adapter = DisciplinasAdapter(classes)
                swiperefresh.isRefreshing = false
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
            if(!cd.isConnectingToInternet(view!!.context)){
                val snackbar = Snackbar.make(view!!, "Verifique sua conexão com a internet", Snackbar.LENGTH_LONG)
                snackbar.show()
                swiperefresh.isRefreshing = false
                return
            }
            for (it in classes) {
                val request = Request.Builder()
                    .url("""https://academico.quixada.ufc.br/ServletCentral?comando=CmdListarFrequenciaTurmaAluno&id=${it.id}""")
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
                        var credits = serializer.parseClassPlan(it.id, res)
                        it.credits = credits.size * 2
                        var studentClass = Class(it.id, it.name, it.professorName, it.professorEmail, it.percentageAttendance, it.credits, it.missed, it.totalAttendance)
                        database.studentDao().insertClass(studentClass)
                        var news = serializer.parseNews(it.id, res)
                        var classPlan = serializer.parseClassPlan(it.id, res)
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
        val client = OkHttpClient()
        withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val serializer = Serializer()
                var grades = serializer.parseGrades(id, response.body()!!.string())
                grades.forEach {
                    database.studentDao().insertGrade(it)
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
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
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