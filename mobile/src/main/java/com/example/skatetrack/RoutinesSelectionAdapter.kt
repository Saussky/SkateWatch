package com.example.skatetrack

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RoutineSelectionAdapter(
    private val routines: List<Routine>,
    private val onDeleteConfirmed: (Int) -> Unit
) : RecyclerView.Adapter<RoutineSelectionAdapter.RoutineViewHolder>() {

    class RoutineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val routineNameTextView: TextView = view.findViewById(R.id.routineNameTextView)
        val editButton: Button = view.findViewById(R.id.editButton)
        val viewStatsButton: Button = view.findViewById(R.id.viewStatsButton)
        val deleteButton: Button = view.findViewById(R.id.deleteButton)
        val confirmDeleteView: View = view.findViewById(R.id.confirmDeleteView)
        val confirmDeleteYesButton: Button = view.findViewById(R.id.confirmDeleteYesButton)
        val confirmDeleteNoButton: Button = view.findViewById(R.id.confirmDeleteNoButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_routine_selection, parent, false)
        return RoutineViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        val routine = routines[position]
        holder.routineNameTextView.text = routine.name

        holder.editButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EditRoutineActivity::class.java)
            intent.putExtra("routineName", routine.name)
            context.startActivity(intent)
        }
        holder.viewStatsButton.setOnClickListener {
            // Handle view stats
        }
        holder.deleteButton.setOnClickListener {
            holder.confirmDeleteView.visibility = View.VISIBLE
        }
        holder.confirmDeleteYesButton.setOnClickListener {
            onDeleteConfirmed(position)
        }
        holder.confirmDeleteNoButton.setOnClickListener {
            holder.confirmDeleteView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = routines.size
}
