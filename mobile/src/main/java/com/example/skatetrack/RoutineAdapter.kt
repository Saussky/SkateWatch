package com.example.skatetrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RoutineAdapter(
    private val routines: MutableList<Trick>,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder>() {

    class RoutineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stanceTextView: TextView = view.findViewById(R.id.stanceTextView)
        val trickTextView: TextView = view.findViewById(R.id.trickTextView)
        val landingGoalTextView: TextView = view.findViewById(R.id.landingGoalTextView)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_routine, parent, false)
        return RoutineViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        val routine = routines[position]
        holder.stanceTextView.text = routine.stance
        holder.trickTextView.text = routine.trick
        holder.landingGoalTextView.text = "Goal: ${routine.landingGoal}"
        holder.deleteButton.setOnClickListener {
            onDelete(position)
        }
    }

    override fun getItemCount(): Int = routines.size
}
