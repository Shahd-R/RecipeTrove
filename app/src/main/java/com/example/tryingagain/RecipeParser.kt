package com.example.tryingagain

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

object RecipeParser {

    fun parseRecipes(context: Context, fileName: String): List<Recipe> {
        val recipes = mutableListOf<Recipe>()
        try {
            val inputStream = context.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            var title = ""
            var ingredients = mutableListOf<String>()
            var instructions = StringBuilder()
            var readingInstructions = false

            while (reader.readLine().also { line = it } != null) {
                line = line!!.trim()
                when {
                    line!!.startsWith("Title:") -> {
                        if (title.isNotEmpty()) {
                            recipes.add(Recipe(title, ingredients, instructions.toString().trim()))
                            ingredients = mutableListOf()
                            instructions = StringBuilder()
                        }
                        title = line!!.substring(6).trim()
                        readingInstructions = false
                    }
                    line!!.startsWith("Ingredients:") -> {
                        readingInstructions = false
                    }
                    line!!.startsWith("Instructions:") -> {
                        readingInstructions = true
                    }
                    line!!.startsWith("-") -> {
                        if (!readingInstructions) {
                            ingredients.add(line!!.substring(1).trim())
                        } else {
                            instructions.append(line).append("\n")
                        }
                    }
                    line!!.isNotEmpty() -> {
                        instructions.append(line).append("\n")
                    }
                }
            }
            if (title.isNotEmpty()) {
                recipes.add(Recipe(title, ingredients, instructions.toString().trim()))
            }
            reader.close()
        } catch (e: Exception) {
            Log.e("RecipeParser", "Error reading file", e)
        }
        Log.d("RecipeParser", "Parsed recipes: $recipes")
        return recipes
    }
}
