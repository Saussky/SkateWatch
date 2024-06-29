package com.example.skatetrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StatsAdapter(private val trickStats: List<StatsActivity.TrickStats>) :
    RecyclerView.Adapter<StatsAdapter.StatsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stat, parent, false)
        return StatsViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatsViewHolder, position: Int) {
        val stats = trickStats[position]
        holder.trickNameTextView.text = stats.trickName
        holder.attemptsTextView.text = "Attempts: ${stats.noLands + stats.lands}"
        holder.landsTextView.text = "Lands: ${stats.lands}"
    }

    override fun getItemCount(): Int {
        return trickStats.size
    }

    class StatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trickNameTextView: TextView = itemView.findViewById(R.id.textView_trickName)
        val attemptsTextView: TextView = itemView.findViewById(R.id.textView_attempts)
        val landsTextView: TextView = itemView.findViewById(R.id.textView_lands)
    }
}