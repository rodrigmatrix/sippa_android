package com.rodrigmatrix.sippa

import android.content.Context
import android.net.Uri
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
import kotlinx.android.synthetic.main.fragment_disciplinas.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.support.v4.runOnUiThread
import java.lang.Exception

class DisciplinasFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
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
            .build()

        swiperefresh!!.setOnRefreshListener {
            Thread{
                val jsession = database.StudentDao().getStudent().jsession
                setClasses(jsession, database)
            }.start()
        }

        Thread {
            val jsession = database.StudentDao().getStudent().jsession
            runOnUiThread {
                swiperefresh!!.isRefreshing = true
            }
            setClasses(jsession, database)
        }.start()
    }
//    private fun isConnectionExpired(res: String){
//        if(res.contains("Atenção: Seu tempo de conexão expirou.")){
//            runOnUiThread {
//                val snackbar = Snackbar.make(view!!, "Tempo expirou. Faça login novamente", Snackbar.LENGTH_LONG)
//                    snackbar.setAction("Login", view!!.onClick {
//
//                    })
//                snackbar.show()
//                swiperefresh.isRefreshing = false
//            }
//        }
//    }

    private fun setClasses(jsession: String, database: StudentsDatabase){
        Thread {
            val cd = ConnectionDetector()
            val serializer = Serializer()
            val classes = serializer.parseClasses(database.StudentDao().getStudent().responseHtml)
            val client = OkHttpClient()
            var parsed = true
            if(!cd.isConnectingToInternet(view!!.context)){
                runOnUiThread {
                    val snackbar = Snackbar.make(view!!, "Verifique sua conexão com a internet", Snackbar.LENGTH_LONG)
                    snackbar.show()
                    swiperefresh.isRefreshing = false
                }
                return@Thread
            }
            for (it in classes){
                val request = Request.Builder()
                    .url("""https://sistemas.quixada.ufc.br/apps/ServletCentral?comando=CmdListarFrequenciaTurmaAluno&id=${it.id}""")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Cookie", jsession)
                    .build()
                try{
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        val res = response.body()!!.string()
                        it.attendance = serializer.parseAttendance(res)
                        it.professorEmail = serializer.parseProfessorEmail(res)
                    }
                    else{
                        parsed = false
                        runOnUiThread {
                            swiperefresh.isRefreshing = false
                            val snackbar = Snackbar.make(view!!, "Verifique sua conexão com a internet", Snackbar.LENGTH_LONG)
                            snackbar.show()
                        }
                        break
                    }
                }catch (e: Exception){
                    println(e)
                    parsed = false
                    runOnUiThread {
                        swiperefresh.isRefreshing = false
                        val snackbar = Snackbar.make(view!!, "Verifique sua conexão com a internet", Snackbar.LENGTH_LONG)
                        snackbar.show()
                    }
                    break
                }

            }
            if(parsed){
                runOnUiThread {
                    try {
                        recyclerView_disciplinas.layoutManager = LinearLayoutManager(context)
                        recyclerView_disciplinas.adapter = DisciplinasAdapter(classes)
                        swiperefresh.isRefreshing = false
                    }catch (e: Exception){
                        runOnUiThread {
                            swiperefresh.isRefreshing = false
                            val snackbar = Snackbar.make(view!!, "Erro ao exibir dados. Tente novamente", Snackbar.LENGTH_LONG)
                            snackbar.show()
                        }
                        println(e)
                    }
                }
            }
        }.start()
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
