package com.rodrigmatrix.sippa.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rodrigmatrix.sippa.R
import com.rodrigmatrix.sippa.Serializer.News
import kotlinx.android.synthetic.main.noticia_row.view.*

class NoticiasAdapter(private val newsList: MutableList<News>): RecyclerView.Adapter<NotasViewHolder>() {

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotasViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val notasRow = layoutInflater.inflate(R.layout.noticia_row, parent, false)
        return NotasViewHolder(notasRow)
    }

    override fun onBindViewHolder(holder: NotasViewHolder, position: Int) {
        val news = newsList[position]
        holder.view.news_date.text = news.date
        holder.view.news_content.text = news.content
    }
}

class NotasViewHolder(val view: View): RecyclerView.ViewHolder(view){
    init {


    }
}