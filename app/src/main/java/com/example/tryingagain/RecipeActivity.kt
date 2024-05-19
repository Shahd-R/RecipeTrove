package com.example.tryingagain

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecipeActivity : AppCompatActivity() {

    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var filterStarredButton: Button
    private var showingStarredRecipes = false

    private lateinit var allRecipes: List<Recipe>
    private lateinit var displayedRecipes: List<Recipe>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        recipeRecyclerView = findViewById(R.id.recipe_recycler_view)
        recipeRecyclerView.layoutManager = LinearLayoutManager(this)

        filterStarredButton = findViewById(R.id.filter_starred_button)
        filterStarredButton.setOnClickListener {
            toggleRecipeView()
        }

        val backButton = findViewById<Button>(R.id.back_to_pantry_button)
        backButton.setOnClickListener {
            finish()
        }

        val addToChecklistButton = findViewById<Button>(R.id.add_to_checklist_button)
        addToChecklistButton.setOnClickListener {
            val intent = Intent(this, ChecklistActivity::class.java)
            startActivity(intent)
        }

        val pantryItems = intent.getStringArrayListExtra("pantryItems") ?: arrayListOf()
        Log.d("RecipeActivity", "Pantry items: $pantryItems")

        allRecipes = RecipeParser.parseRecipes(this, "formatted_recipes.txt")
        Log.d("RecipeActivity", "All recipes: $allRecipes")

        displayedRecipes = allRecipes.filter { recipe ->
            pantryItems.all { pantryItem ->
                val singularForm = pantryItem.trim().removeSuffix("s").toLowerCase()
                val pluralForm = singularForm + "s"
                recipe.ingredients.any { ingredient ->
                    ingredient.toLowerCase().contains(singularForm) ||
                            ingredient.toLowerCase().contains(pluralForm)
                }
            }
        }

        Log.d("RecipeActivity", "Displayed recipes: $displayedRecipes")
        recipeAdapter = RecipeAdapter(this, displayedRecipes)
        recipeRecyclerView.adapter = recipeAdapter
    }

    private fun toggleRecipeView() {
        if (showingStarredRecipes) {
            recipeAdapter.setRecipes(displayedRecipes)
            filterStarredButton.text = "Filter by Starred Recipes"
        } else {
            recipeAdapter.setRecipes(allRecipes)
            recipeAdapter.filterStarredRecipes()
            filterStarredButton.text = "Back to Recipes"
        }
        showingStarredRecipes = !showingStarredRecipes
    }
}
