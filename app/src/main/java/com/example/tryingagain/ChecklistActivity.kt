package com.example.tryingagain

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ChecklistActivity : AppCompatActivity() {

    private lateinit var checklistRecyclerView: RecyclerView
    private lateinit var checklistAdapter: ChecklistAdapter
    private lateinit var itemInput: EditText
    private lateinit var addItemButton: Button
    private val checklistItems = mutableListOf<ChecklistItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checklist)

        itemInput = findViewById(R.id.item_input)
        addItemButton = findViewById(R.id.add_item_button)
        checklistRecyclerView = findViewById(R.id.checklist_recycler_view)
        checklistRecyclerView.layoutManager = LinearLayoutManager(this)
        checklistAdapter = ChecklistAdapter(checklistItems) { item ->
            removeChecklistItem(item)
        }
        checklistRecyclerView.adapter = checklistAdapter

        loadChecklistItems()

        addItemButton.setOnClickListener {
            val itemText = itemInput.text.toString()
            if (!TextUtils.isEmpty(itemText)) {
                val newItem = ChecklistItem(itemText)
                checklistItems.add(newItem)
                checklistAdapter.notifyItemInserted(checklistItems.size - 1)
                itemInput.text.clear()
                saveChecklistItems()
            }
        }

        findViewById<Button>(R.id.back_to_recipes_button).setOnClickListener {
            finish()
        }
    }

    private fun saveChecklistItems() {
        val sharedPreferences = getSharedPreferences("checklist_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(checklistItems)
        editor.putString("checklist_items", json)
        editor.apply()
    }

    private fun loadChecklistItems() {
        val sharedPreferences = getSharedPreferences("checklist_prefs", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("checklist_items", null)
        if (json != null) {
            val type = object : TypeToken<MutableList<ChecklistItem>>() {}.type
            val items: MutableList<ChecklistItem> = gson.fromJson(json, type)
            checklistItems.addAll(items)
            checklistAdapter.notifyDataSetChanged()
        }
    }

    private fun removeChecklistItem(item: ChecklistItem) {
        checklistAdapter.removeItem(item)
        saveChecklistItems()
    }
}
