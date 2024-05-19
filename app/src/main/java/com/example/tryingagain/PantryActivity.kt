package com.example.tryingagain

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PantryActivity : AppCompatActivity() {

    private lateinit var detectedObjects: ArrayList<String>
    private lateinit var pantryTextView: TextView

    companion object {
        private const val EDIT_PANTRY_REQUEST_CODE = 102
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_pantry)

        detectedObjects = intent.getStringArrayListExtra("detectedObjects") ?: arrayListOf()

        pantryTextView = findViewById(R.id.pantry_text_view)
        displayItemsWithNumbers(detectedObjects, pantryTextView)

        val editButton = findViewById<Button>(R.id.edit_pantry_button)
        editButton.setOnClickListener {
            val intent = Intent(this, EditPantryActivity::class.java)
            intent.putStringArrayListExtra("pantryItems", detectedObjects)
            startActivityForResult(intent, EDIT_PANTRY_REQUEST_CODE)
        }

        val proceedToRecipesButton = findViewById<Button>(R.id.proceed_to_recipes_button)
        proceedToRecipesButton.setOnClickListener {
            val intent = Intent(this, RecipeActivity::class.java)
            intent.putStringArrayListExtra("pantryItems", detectedObjects)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_PANTRY_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.getStringArrayListExtra("updatedPantryItems")?.let { updatedItems ->
                detectedObjects = updatedItems
                displayItemsWithNumbers(detectedObjects, pantryTextView)
            }
        }
    }

    private fun displayItemsWithNumbers(items: List<String>, textView: TextView) {
        val numberedItems = StringBuilder()
        for ((index, item) in items.withIndex()) {
            if (item.isNotEmpty()) {
                val numberedItem = "${index + 1}. $item\n"
                numberedItems.append(numberedItem)
            }
        }
        textView.text = if (numberedItems.isNotEmpty()) numberedItems.toString() else "No items detected"
    }
}
