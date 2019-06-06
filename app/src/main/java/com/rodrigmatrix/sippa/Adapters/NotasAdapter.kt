package com.rodrigmatrix.sippa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rodrigmatrix.sippa.serializer.Grade
import kotlinx.android.synthetic.main.nota_row.view.*


class NotasAdapter(private val notas: MutableList<Grade>): RecyclerView.Adapter<NotasViewHolder>() {

    override fun getItemCount(): Int {
        return notas.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotasViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val notasRow = layoutInflater.inflate(R.layout.nota_row, parent, false)
        return NotasViewHolder(notasRow)
    }

    override fun onBindViewHolder(holder: NotasViewHolder, position: Int) {
        val notasData = notas[position]
        holder.view.nome_nota_text.text = notasData.name
        if(notasData.grade == ""){
            holder.view.nota_text.text = "Não cadastrada"
        }
        else{
            holder.view.nota_text.text = notasData.grade
        }
    }
}

class NotasViewHolder(val view: View): RecyclerView.ViewHolder(view){
    init {


    }
}