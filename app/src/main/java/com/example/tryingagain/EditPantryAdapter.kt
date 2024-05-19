package com.example.tryingagain

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.tryingagain.R

class EditPantryAdapter(private var pantryItems: ArrayList<String>) :
    RecyclerView.Adapter<EditPantryAdapter.PantryViewHolder>() {

    inner class PantryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemEditText: EditText = itemView.findViewById(R.id.item_edit_text)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)

        init {
            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    pantryItems.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, pantryItems.size)
                }
            }

            itemEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        pantryItems[position] = s.toString()
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PantryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_edit_pantry, parent, false)
        return PantryViewHolder(view)
    }

    override fun onBindViewHolder(holder: PantryViewHolder, position: Int) {
        holder.itemEditText.setText(pantryItems[position])
    }

    override fun getItemCount(): Int {
        return pantryItems.size
    }

    fun getItems(): ArrayList<String> {
        return pantryItems
    }
}
