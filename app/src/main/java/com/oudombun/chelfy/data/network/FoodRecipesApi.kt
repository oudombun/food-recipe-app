package com.oudombun.chelfy.data.network

import com.oudombun.chelfy.models.AnalyzedInstruction
import com.oudombun.chelfy.models.FoodJoke
import com.oudombun.chelfy.models.FoodRecipe
import com.oudombun.chelfy.models.MealPlanResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface FoodRecipesApi {

    @GET("/recipes/complexSearch")
    suspend fun getRecipes(
        @QueryMap queries: Map<String, String>
    ): Response<FoodRecipe>

    @GET("recipes/{id}/analyzedInstructions")
    suspend fun getAnalyzedInstructions(
        @Path("id") recipeId: String,
        @Query("apiKey") apiKey: String?
    ): Response<List<AnalyzedInstruction>>

    @GET("/recipes/complexSearch")
    suspend fun searchRecipes(
        @QueryMap searchQuery: Map<String, String>
    ): Response<FoodRecipe>

    @GET("food/jokes/random")
    suspend fun getFoodJoke(
        @Query("apiKey") apiKey: String
    ): Response<FoodJoke>

    @GET("mealplanner/generate")
    suspend fun generateMealPlan(
        @Query("apiKey") apiKey: String,
        @Query("timeFrame") timeFrame: String = "day",
        @Query("targetCalories") targetCalories: Int? = null,
        @Query("diet") diet: String? = null,
        @Query("exclude") exclude: String? = null
    ): Response<MealPlanResponse>
}