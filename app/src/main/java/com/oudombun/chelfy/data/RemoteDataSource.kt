package com.oudombun.chelfy.data

import com.oudombun.chelfy.data.network.FoodRecipesApi
import com.oudombun.chelfy.models.AnalyzedInstruction
import com.oudombun.chelfy.models.FoodJoke
import com.oudombun.chelfy.models.FoodRecipe
import com.oudombun.chelfy.models.MealPlanResponse
import retrofit2.Response
import retrofit2.http.Query
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val foodRecipesApi: FoodRecipesApi
) {

    suspend fun getRecipes(queries: Map<String, String>): Response<FoodRecipe> {
        return foodRecipesApi.getRecipes(queries)
    }
    suspend fun getAnalyzedInstructions(recipeId:String, apiKey:String): Response<List<AnalyzedInstruction>> {
        return foodRecipesApi.getAnalyzedInstructions(recipeId, apiKey)
    }

    suspend fun searchRecipes(searchQuery: Map<String, String>): Response<FoodRecipe> {
        return foodRecipesApi.searchRecipes(searchQuery)
    }

    suspend fun getFoodJoke(apiKey: String): Response<FoodJoke> {
        return foodRecipesApi.getFoodJoke(apiKey)
    }

    suspend fun getMealPlan(
        apiKey: String,
        timeFrame: String = "day",
        targetCalories: Int? = null, // Make targetCalories nullable
        diet: String? = null,
        exclude: String? = null
    ): Response<MealPlanResponse> {
        return foodRecipesApi.generateMealPlan(apiKey, timeFrame, targetCalories, diet, exclude)
    }
}