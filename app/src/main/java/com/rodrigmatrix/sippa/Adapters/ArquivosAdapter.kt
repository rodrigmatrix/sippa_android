package com.rodrigmatrix.sippa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rodrigmatrix.sippa.serializer.File
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


    }
}