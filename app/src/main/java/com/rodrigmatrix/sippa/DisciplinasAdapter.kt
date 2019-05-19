package com.rodrigmatrix.sippa

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.rodrigmatrix.sippa.Serializer.Class
import kotlinx.android.synthetic.main.disciplina_row.view.*



class DisciplinasAdapter(val classes: MutableList<Class>): RecyclerView.Adapter<DisciplinasViewHolder>() {
    override fun getItemCount(): Int {
        return classes.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisciplinasViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val disciplaRow = layoutInflater.inflate(R.layout.disciplina_row, parent, false)
        return DisciplinasViewHolder(disciplaRow)
    }

    override fun onBindViewHolder(holder: DisciplinasViewHolder, position: Int) {
        val classData = classes[position]
        holder?.view?.id_disciplina?.text = classData.id
        holder?.view?.class_name_text?.text = classData.name
        holder?.view?.class_professor_text?.text = classData.professor
        holder?.view?.percentage_attendance_text?.text = "Frequência: " + classData.percentageAttendance + "%"
        holder?.view?.class_missed_text?.text = "Faltas: " + (classData.attendance.totalMissed/2) + " Aula(s)"
        holder?.view?.professor_email_text?.text = classData.professorEmail
    }
}

class DisciplinasViewHolder(val view: View): RecyclerView.ViewHolder(view){
    init {
        view.see_more_button.setOnClickListener {
            val intent = Intent(view.context, DisciplinaActivity::class.java)
            intent.putExtra("id", view.id_disciplina.text.toString())
            intent.putExtra("option", "all")
            view.context.startActivity(intent)
            //println("ver mais pressed id: " + view?.id_disciplina.text.toString())
        }
        view.see_grades_button.setOnClickListener {
            val intent = Intent(view.context, DisciplinaActivity::class.java)
            intent.putExtra("id", view.id_disciplina.text.toString())
            intent.putExtra("option", "grades")
            view.context.startActivity(intent)
            //println("ver notas pressed id: " + view?.id_disciplina.text.toString())
        }

    }
}