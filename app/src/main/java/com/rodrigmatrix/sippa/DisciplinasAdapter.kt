package com.rodrigmatrix.sippa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class DisciplinasAdapter: RecyclerView.Adapter<CustomViewHolder>() {
    override fun getItemCount(): Int {
        return 6
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val disciplaRow = layoutInflater.inflate(R.layout.disciplina_row, parent, false)
        return CustomViewHolder(disciplaRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        //holder?.view.?.
    }
}

class CustomViewHolder(v: View): RecyclerView.ViewHolder(v){
}