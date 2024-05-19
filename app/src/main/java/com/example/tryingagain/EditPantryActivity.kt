package com.example.tryingagain

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EditPantryActivity : AppCompatActivity() {

    private lateinit var editPantryAdapter: EditPantryAdapter
    private lateinit var pantryItems: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pantry)

        Log.d("EditPantryActivity", "onCreate called")

        pantryItems = intent.getStringArrayListExtra("pantryItems") ?: arrayListOf()
        Log.d("EditPantryActivity", "Pantry items received: $pantryItems")

        val editPantryRecyclerView = findViewById<RecyclerView>(R.id.edit_pantry_recycler_view)
        editPantryRecyclerView.layoutManager = LinearLayoutManager(this)
        editPantryAdapter = EditPantryAdapter(pantryItems)
        editPantryRecyclerView.adapter = editPantryAdapter

        val addItemButton = findViewById<Button>(R.id.add_item_button)
        addItemButton.setOnClickListener {
            Log.d("EditPantryActivity", "Add Item button clicked")
            pantryItems.add("")
            editPantryAdapter.notifyItemInserted(pantryItems.size - 1)
        }

        val saveButton = findViewById<Button>(R.id.save_button)
        saveButton.setOnClickListener {
            Log.d("EditPantryActivity", "Save button clicked")
            val updatedItems = editPantryAdapter.getItems()
            val resultIntent = Intent()
            resultIntent.putStringArrayListExtra("updatedPantryItems", updatedItems)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}
