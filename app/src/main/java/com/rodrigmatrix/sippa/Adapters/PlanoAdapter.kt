package com.rodrigmatrix.sippa.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rodrigmatrix.sippa.R
import com.rodrigmatrix.sippa.Serializer.ClassPlan
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

    override fun onBindViewHolder(holder: PlanoViewHolder, position: Int) {
        val aulaData = aulas[position]
        holder.view.data_aula_text?.text =  aulaData.classNumber + "    " + aulaData.classDate
        holder.view.diario_aula_text.text = aulaData.classDiary
        holder.view.plano_aula_text.text = aulaData.ClassPlanned
        holder.view.presenca_aula_text.text = aulaData.attendance

    }
}

class PlanoViewHolder(val view: View): RecyclerView.ViewHolder(view){
    init {


    }
}
