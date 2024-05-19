package com.example.tryingagain

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RecipeDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        val recipeTitle = intent.getStringExtra("recipeTitle")
        val recipeIngredients = intent.getStringArrayListExtra("recipeIngredients")
        val recipeInstructions = intent.getStringExtra("recipeInstructions")

        val titleTextView = findViewById<TextView>(R.id.recipe_detail_title)
        val ingredientsTextView = findViewById<TextView>(R.id.recipe_detail_ingredients)
        val instructionsTextView = findViewById<TextView>(R.id.recipe_detail_instructions)
        val backButton = findViewById<Button>(R.id.back_to_recipes_button)

        titleTextView.text = recipeTitle
        ingredientsTextView.text = recipeIngredients?.joinToString(separator = "\n") { "- $it" }
        instructionsTextView.text = recipeInstructions

        backButton.setOnClickListener {
            finish()
        }
    }
}
