package com.example.tryingagain

data class Recipe(
    val name: String,
    val ingredients: List<String>,
    val instructions: String,
    var isFavorite: Boolean = false
)
