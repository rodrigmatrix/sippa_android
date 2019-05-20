package com.rodrigmatrix.sippa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rodrigmatrix.sippa.serializer.HorasComplementares
import kotlinx.android.synthetic.main.horas_row.view.*

class HorasAdapter(private val horas: MutableList<HorasComplementares>): RecyclerView.Adapter<HorasViewHolder>() {
    override fun getItemCount(): Int {
        return horas.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorasViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val horasRow = layoutInflater.inflate(R.layout.horas_row, parent, false)
        return HorasViewHolder(horasRow)
    }

    override fun onBindViewHolder(holder: HorasViewHolder, position: Int) {
        val horasData = horas[position]
        holder.view.atividade_horas_text?.text = horasData.name
        holder.view.cadastro_horas_text?.text = horasData.professor
        holder.view.horas_text?.text = "Horas totais: " + horasData.horas
    }
}

class HorasViewHolder(val view: View): RecyclerView.ViewHolder(view){
    init {


    }
}
