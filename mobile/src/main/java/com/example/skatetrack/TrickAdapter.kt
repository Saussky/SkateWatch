package com.example.skatetrack

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TrickAdapter(
    private val tricks: MutableList<Trick>,
    private val onDelete: (Int) -> Unit,
    private val onMoveUp: (Int) -> Unit
) : RecyclerView.Adapter<TrickAdapter.TrickViewHolder>() {

    class TrickViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stanceTextView: TextView = view.findViewById(R.id.stanceTextView)
        val trickTextView: TextView = view.findViewById(R.id.trickTextView)
        val landingGoalEditText: EditText = view.findViewById(R.id.landingGoalEditText)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
        val moveUpButton: ImageButton = view.findViewById(R.id.moveUpButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrickViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trick, parent, false)
        return TrickViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrickViewHolder, position: Int) {
        val trick = tricks[position]
        holder.stanceTextView.text = trick.stance
        holder.trickTextView.text = trick.trick
        holder.landingGoalEditText.setText(trick.landingGoal.toString())

        holder.landingGoalEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val newGoal = s.toString().toIntOrNull() ?: 0
                tricks[position] = trick.copy(landingGoal = newGoal)
            }
        })

        holder.deleteButton.setOnClickListener {
            onDelete(position)
        }

        holder.moveUpButton.setOnClickListener {
            onMoveUp(position)
        }
    }

    override fun getItemCount(): Int = tricks.size
}
