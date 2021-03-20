package com.rodrigmatrix.sippa

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rodrigmatrix.sippa.persistance.Class
import kotlinx.android.synthetic.main.disciplina_row.view.*
import org.jetbrains.anko.textColor


class DisciplinasAdapter(private val classes: MutableList<Class>): RecyclerView.Adapter<DisciplinasViewHolder>() {
    override fun getItemCount(): Int {
        return classes.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisciplinasViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val disciplaRow = layoutInflater.inflate(R.layout.disciplina_row, parent, false)
        return DisciplinasViewHolder(disciplaRow)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DisciplinasViewHolder, position: Int) {
        val classData = classes[position]
        holder.view.id_disciplina?.text = classData.id
        holder.view.class_name_text?.text = classData.name
        holder.view.class_professor_text?.text = classData.professorName
        holder.view.percentage_attendance_text?.text = "Frequência: " + classData.percentageAttendance + "%"
        holder.view.class_missed_text?.text = "Faltas: " + (classData.missed/2) + " Aula(s)"
        holder.view.professor_email_text?.text = classData.professorEmail
        convertColors(classData, holder.view.class_name_text, holder.view.class_missed_text, holder.view.class_missed_text.context)
    }
    @SuppressLint("SetTextI18n")
    private fun convertColors(classData: Class, classNameView: TextView, missed: TextView, context: Context){
        if(classData.credits != 2){
            val cmd = (classData.credits/2) * 0.25
            val missedClasses = classData.missed/2
            val missedText = missed.text
            val className = classNameView.text
            classNameView.text = "$className - ${classData.credits} Horas"
            missed.text = "$missedText/Restante: ${(cmd - missedClasses).toInt()}"
            when {
                cmd.toInt() == 12 -> {
                    when{
                        missedClasses <= 6 ->{
                            missed.textColor = ContextCompat.getColor(context, R.color.colorSisac)
                        }
                        missedClasses in 7..9 ->{
                            missed.textColor = ContextCompat.getColor(context, R.color.Yellow)
                        }
                        missedClasses in 9..12 ->{
                            missed.textColor = ContextCompat.getColor(context, R.color.Orange)
                        }
                        else -> {
                            missed.textColor = ContextCompat.getColor(context, R.color.Red)
                            missed.text = "$missedText/Reprovado"
                        }
                    }
                }
                cmd.toInt() == 8 -> {
                    when{
                        missedClasses <= 3 ->{
                            missed.textColor = ContextCompat.getColor(context, R.color.colorSisac)
                        }
                        missedClasses in 4..6 ->{
                            missed.textColor = ContextCompat.getColor(context, R.color.Yellow)
                        }
                        missedClasses in 7..8 ->{
                            missed.textColor = ContextCompat.getColor(context, R.color.Orange)
                        }
                        else -> {
                            missed.textColor = ContextCompat.getColor(context, R.color.Red)
                            missed.text = "$missedText/Reprovado"
                        }
                    }
                }
                else -> {
                    when{
                        missedClasses <= 2 ->{
                            missed.textColor = ContextCompat.getColor(context, R.color.colorSisac)
                        }
                        missedClasses == 3 ->{
                            missed.textColor = ContextCompat.getColor(context, R.color.Yellow)
                        }
                        missedClasses == 4 ->{
                            missed.textColor = ContextCompat.getColor(context, R.color.Orange)
                        }
                        else -> {
                            missed.textColor = ContextCompat.getColor(context, R.color.Red)
                            missed.text = "$missedText/Reprovado"
                        }
                    }
                }
            }
        }
    }
}

class DisciplinasViewHolder(val view: View): RecyclerView.ViewHolder(view){
    init {
        view.card.setOnClickListener {
            view.see_more_button.performClick()
        }
        view.see_more_button.setOnClickListener {
            val intent = Intent(view.context, DisciplinaActivity::class.java)
            intent.putExtra("id", view.id_disciplina.text.toString())
            intent.putExtra("name", view.class_name_text.text.toString())
            intent.putExtra("option", "all")
            view.context.startActivity(intent)
        }
        view.see_grades_button.setOnClickListener {
            val intent = Intent(view.context, DisciplinaActivity::class.java)
            intent.putExtra("id", view.id_disciplina.text.toString())
            intent.putExtra("name", view.class_name_text.text.toString())
            intent.putExtra("option", "grades")
            view.context.startActivity(intent)
        }

    }
}
