package com.example.tryingagain

import android.content.Context
import android.content.SharedPreferences
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecipeAdapter(private val context: Context, private var recipes: List<Recipe>) :
    RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("starred_recipes", Context.MODE_PRIVATE)
    private var originalRecipes: List<Recipe> = recipes

    init {
        loadStarredRecipes()
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val recipeNameTextView: TextView = itemView.findViewById(R.id.recipe_name_text_view)
        val arrowIcon: ImageView = itemView.findViewById(R.id.arrow_icon)
        val starIcon: ImageView = itemView.findViewById(R.id.star_icon)

        init {
            itemView.setOnClickListener(this)
            starIcon.setOnClickListener {
                toggleFavorite()
            }
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val selectedRecipe = recipes[position]
                val intent = Intent(itemView.context, RecipeDetailActivity::class.java).apply {
                    putExtra("recipeTitle", selectedRecipe.name)
                    putStringArrayListExtra("recipeIngredients", ArrayList(selectedRecipe.ingredients))
                    putExtra("recipeInstructions", selectedRecipe.instructions)
                }
                itemView.context.startActivity(intent)
            }
        }

        private fun toggleFavorite() {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val selectedRecipe = recipes[position]
                selectedRecipe.isFavorite = !selectedRecipe.isFavorite
                updateStarIcon(selectedRecipe.isFavorite)
                saveStarredRecipe(selectedRecipe)
            }
        }

        fun updateStarIcon(isFavorite: Boolean) {
            if (isFavorite) {
                starIcon.setImageResource(R.drawable.ic_star_filled)
            } else {
                starIcon.setImageResource(R.drawable.ic_star_empty)
            }
        }

        private fun saveStarredRecipe(recipe: Recipe) {
            val editor = sharedPreferences.edit()
            editor.putBoolean(recipe.name, recipe.isFavorite)
            editor.apply()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.recipeNameTextView.text = recipe.name
        holder.updateStarIcon(recipe.isFavorite)
    }

    override fun getItemCount() = recipes.size

    private fun loadStarredRecipes() {
        for (recipe in originalRecipes) {
            recipe.isFavorite = sharedPreferences.getBoolean(recipe.name, false)
        }
    }

    fun filterStarredRecipes() {
        recipes = originalRecipes.filter { it.isFavorite }
        notifyDataSetChanged()
    }

    fun resetRecipes() {
        recipes = originalRecipes
        notifyDataSetChanged()
    }

    fun setRecipes(newRecipes: List<Recipe>) {
        recipes = newRecipes
        originalRecipes = newRecipes
        loadStarredRecipes()
        notifyDataSetChanged()
    }
}
