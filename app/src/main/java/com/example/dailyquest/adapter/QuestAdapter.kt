package com.example.dailyquest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyquest.R
import com.example.dailyquest.model.Quest

class QuestAdapter(private val questList: List<Quest>) :
    RecyclerView.Adapter<QuestAdapter.QuestViewHolder>() {

    class QuestViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val category: TextView = view.findViewById(R.id.quest_list_category)
        val title: TextView = view.findViewById(R.id.quest_list_title)
        val period: TextView = view.findViewById(R.id.quest_list_period)
        val xp: TextView = view.findViewById(R.id.quest_list_xp)
        val image: ImageView = view.findViewById(R.id.quest_list_circle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.quest_list, parent, false)
        return QuestViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestViewHolder, position: Int) {
        val quest = questList[position]
        holder.category.text = quest.category
        holder.title.text = quest.title
        holder.period.text = quest.period
        holder.xp.text = quest.xp.toString() + "xp"
        holder.image.setImageResource(R.drawable.icon_blue_circle)
    }

    override fun getItemCount() = questList.size
}
