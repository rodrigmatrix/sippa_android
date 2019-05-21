package com.rodrigmatrix.sippa

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context.DOWNLOAD_SERVICE
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.*
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Query
import androidx.room.Room
import com.rodrigmatrix.sippa.persistance.StudentsDatabase
import com.rodrigmatrix.sippa.serializer.File
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.arquivo_row.view.*

class ArquivosAdapter(private val arquivos: MutableList<File>): RecyclerView.Adapter<ArquivosViewHolder>() {

    override fun getItemCount(): Int {
        return arquivos.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArquivosViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val arquivosRow = layoutInflater.inflate(R.layout.arquivo_row, parent, false)
        return ArquivosViewHolder(arquivosRow)
    }

    override fun onBindViewHolder(holder: ArquivosViewHolder, position: Int) {
        val arquivoData = arquivos[position]
        holder.view.arquivo_name?.text = arquivoData.name
    }
}

class ArquivosViewHolder(val view: View): RecyclerView.ViewHolder(view){
    init {
        view.download_button.setOnClickListener {
            val url = """https://sistemas.quixada.ufc.br/apps/ServletCentral?comando=CmdLoadArquivo&id=${view.arquivo_name.text}"""
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkSelfPermission(view.context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    makeRequest()
                }
                else{
                    startDownload(url, view.arquivo_name.text.toString())
                }
            }
            else{
                startDownload(url, view.arquivo_name.text.toString())
            }
        }

    }
    private fun startDownload(url: String, name: String){
        Thread{
            val database = Room.databaseBuilder(
                view.context,
                StudentsDatabase::class.java, "database.db")
                .fallbackToDestructiveMigration()
                .build()
            val request = DownloadManager.Request(Uri.parse(url))
            request.addRequestHeader("Cookie", database.StudentDao().getStudent().jsession)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setTitle("Sippa - " + view.arquivo_name.text)
            request.setDescription("Baixando arquivo...")
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name)
            val manager = view.context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
        }.start()
    }
    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            view.context as Activity,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1000)
    }
}