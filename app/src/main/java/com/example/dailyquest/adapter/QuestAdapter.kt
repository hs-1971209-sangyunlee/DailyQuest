package com.example.dailyquest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyquest.R
import com.example.dailyquest.model.Quest

class QuestAdapter(private val questList: List<Quest>, private val onQuestClicked: (Quest) -> Unit) :
    RecyclerView.Adapter<QuestAdapter.QuestViewHolder>() {

    //questList를 사용하기 위해 inner class로 생성
    inner class QuestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val category: TextView = itemView.findViewById(R.id.quest_list_category)
        val title: TextView = itemView.findViewById(R.id.quest_list_title)
        val period: TextView = itemView.findViewById(R.id.quest_list_period)
        val xp: TextView = itemView.findViewById(R.id.quest_list_xp)
        val image: ImageView = itemView.findViewById(R.id.quest_list_circle)

        fun bind(quest: Quest) {
            category.text = quest.category
            title.text = quest.title
            period.text = "~" + quest.period
            xp.text = "${quest.xp}xp"
            image.setImageResource(R.drawable.icon_blue_circle)
            itemView.setOnClickListener {
                onQuestClicked(quest)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.quest_list, parent, false)
        return QuestViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestViewHolder, position: Int) {
        holder.bind(questList[position]);
    }

    override fun getItemCount() = questList.size
}
