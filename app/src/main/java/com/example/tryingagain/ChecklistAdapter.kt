package com.example.tryingagain

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChecklistAdapter(private val items: MutableList<ChecklistItem>, private val onDelete: (ChecklistItem) -> Unit) :
    RecyclerView.Adapter<ChecklistAdapter.ChecklistViewHolder>() {

    class ChecklistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemTextView: TextView = itemView.findViewById(R.id.checklist_item_text)
        val itemCheckBox: CheckBox = itemView.findViewById(R.id.checklist_item_checkbox)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_checklist, parent, false)
        return ChecklistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        val item = items[position]
        holder.itemTextView.text = item.text
        holder.itemCheckBox.isChecked = item.isChecked

        holder.itemCheckBox.setOnCheckedChangeListener { _, isChecked ->
            item.isChecked = isChecked
        }

        holder.deleteButton.setOnClickListener {
            onDelete(item)
        }
    }

    override fun getItemCount() = items.size

    fun removeItem(item: ChecklistItem) {
        val position = items.indexOf(item)
        if (position != -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
