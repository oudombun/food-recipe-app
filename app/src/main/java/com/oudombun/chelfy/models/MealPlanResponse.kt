package com.oudombun.chelfy.models

import com.google.gson.annotations.SerializedName

data class MealPlanResponse(
    @SerializedName("meals")
    val meals: List<Meal>,
    @SerializedName("nutrients")
    val nutrients: Nutrients
)

data class Meal(
    val id: Int,
    val title: String,
    val imageType: String,
    val readyInMinutes: Int,
    val servings: Int,
    val sourceUrl: String
)

data class Nutrients(
    val calories: Double,   // Changed to Double for precision
    val carbohydrates: Double,
    val fat: Double,
    val protein: Double
)
