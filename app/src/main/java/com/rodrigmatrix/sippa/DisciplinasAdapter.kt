package com.rodrigmatrix.sippa

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rodrigmatrix.sippa.Serializer.Class
import kotlinx.android.synthetic.main.disciplina_row.view.*

class DisciplinasAdapter(val classes: MutableList<Class>): RecyclerView.Adapter<CustomViewHolder>() {
    override fun getItemCount(): Int {
        return classes.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val disciplaRow = layoutInflater.inflate(R.layout.disciplina_row, parent, false)
        return CustomViewHolder(disciplaRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val classData = classes[position]
        println(classData)
        holder?.view?.class_name_text?.text = classData.name
        holder?.view?.class_professor_text?.text = classData.professor
        holder?.view?.percentage_attendance_text?.text = "Frequência: " + classData.percentageAttendance + "%"
        holder?.view?.class_missed_text?.text = "Faltas: " + (classData.attendance.totalMissed/2) + " Aula(s)"
    }
}

class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view){
    init {
        view.setOnClickListener {
            val intent = Intent(view.context, DisciplinasAdapter::class.java)
            view.context.startActivity(intent)
        }

    }
}