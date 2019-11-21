package com.rodrigmatrix.sippa.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rodrigmatrix.sippa.R
import com.rodrigmatrix.sippa.persistance.HoraComplementar
import kotlinx.android.synthetic.main.horas_row.view.*

class HorasAdapter(private val horas: MutableList<HoraComplementar>): RecyclerView.Adapter<HorasViewHolder>() {
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
        holder.view.horas_text?.text = horasData.total
        if(horasData.professor.isBlank()){
            holder.view.cadastro_horas_text?.visibility = View.GONE
        }
    }
}

class HorasViewHolder(val view: View): RecyclerView.ViewHolder(view)
