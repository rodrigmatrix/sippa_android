package com.rodrigmatrix.sippa


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
import kotlinx.android.synthetic.main.fragment_plano_aula.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.support.v4.runOnUiThread


class PlanoAulaFragment : Fragment() {
    var id = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        swiperefresh_plano.setColorSchemeResources(R.color.colorPrimary)
        val database = Room.databaseBuilder(
            view.context,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
        Thread {
            val jsession = database.StudentDao().getStudent().jsession
            runOnUiThread {
                swiperefresh_plano!!.isRefreshing = true
            }
            setClass(id, jsession)
        }.start()
        swiperefresh_plano!!.setOnRefreshListener {
            Thread {
                val jsession = database.StudentDao().getStudent().jsession
                runOnUiThread {
                    swiperefresh_plano!!.isRefreshing = true
                }
                setClass(id, jsession)
            }.start()
        }
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
            swiperefresh_plano.isRefreshing = false
            val snackbar =
                Snackbar.make(view!!, "Verifique sua conexão com a internet", Snackbar.LENGTH_LONG)
            snackbar.show()
        }
    }
    private fun setClass(id: String, jsession: String){
        Thread {
            if(!isConnected()){return@Thread}
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("""https://sistemas.quixada.ufc.br/apps/ServletCentral?comando=CmdListarFrequenciaTurmaAluno&id=$id""")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", jsession)
                .build()
            try {
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    showErrorConnection()
                }
                else{
                    val res = response.body()!!.string()
                    val serializer = Serializer()
                    var plan = serializer.parseClassPlan(res)
                    runOnUiThread {
                        swiperefresh_plano.isRefreshing = false
                        recyclerView_plano.layoutManager = LinearLayoutManager(context)
                        recyclerView_plano.adapter = PlanoAdapter(plan)

                    }
                }
            }
            catch(e: Throwable){
                showErrorConnection()
            }
        }.start()
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
