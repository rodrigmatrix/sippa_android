package com.rodrigmatrix.sippa

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rodrigmatrix.sippa.persistance.ClassPlan
import kotlinx.android.synthetic.main.plano_row.view.*

class PlanoAdapter(private val aulas: MutableList<ClassPlan>): RecyclerView.Adapter<PlanoViewHolder>() {
    override fun getItemCount(): Int {
        return aulas.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val planoRow = layoutInflater.inflate(R.layout.plano_row, parent, false)
        return PlanoViewHolder(planoRow)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PlanoViewHolder, position: Int) {
        val aulaData = aulas[position]
        holder.view.data_aula_text?.text =  "${position+1}  ${aulaData.date}"
        holder.view.diario_aula_text.text = aulaData.diary
        holder.view.plano_aula_text.text = aulaData.attendance
        holder.view.presenca_aula_text.text = aulaData.planned

    }
}

class PlanoViewHolder(val view: View): RecyclerView.ViewHolder(view)
