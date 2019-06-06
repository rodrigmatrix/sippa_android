package com.rodrigmatrix.sippa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rodrigmatrix.sippa.serializer.News
import kotlinx.android.synthetic.main.noticia_row.view.*

class NoticiasAdapter(private val newsList: MutableList<News>): RecyclerView.Adapter<NoticiasViewHolder>() {

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticiasViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val notasRow = layoutInflater.inflate(R.layout.noticia_row, parent, false)
        return NoticiasViewHolder(notasRow)
    }

    override fun onBindViewHolder(holder: NoticiasViewHolder, position: Int) {
        val news = newsList[position]
        holder.view.news_date.text = news.date
        holder.view.news_content.text = news.content
    }
}

class NoticiasViewHolder(val view: View): RecyclerView.ViewHolder(view){
    init {


    }
}