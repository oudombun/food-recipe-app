package com.oudombun.chelfy.data.repository

import com.oudombun.chelfy.core.common.NetworkResult
import com.oudombun.chelfy.core.model.AnalyzedInstruction
import com.oudombun.chelfy.core.model.FoodRecipe
import com.oudombun.chelfy.core.model.MealPlanResponse
import com.oudombun.chelfy.core.model.ResultData
import com.oudombun.chelfy.data.local.database.entities.FavoritesEntity
import com.oudombun.chelfy.data.local.database.entities.MealPlanEntity
import com.oudombun.chelfy.data.local.database.entities.RecipesEntity
import com.oudombun.chelfy.data.preferences.MealAndDietType
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    val recipes: Flow<List<RecipesEntity>>
    val favorites: Flow<List<FavoritesEntity>>
    val mealPlan: Flow<List<MealPlanEntity>>
    val filters: Flow<MealAndDietType>

    suspend fun getRecipes(): NetworkResult<FoodRecipe>
    suspend fun search(query: String, filters: MealAndDietType): NetworkResult<FoodRecipe>
    suspend fun getInstructions(id: Int): NetworkResult<List<AnalyzedInstruction>>
    suspend fun generateMealPlan(diet: String, calories: Int?): NetworkResult<MealPlanResponse>
    suspend fun setFavorite(recipe: ResultData, saved: Boolean)
    suspend fun deleteFavorites(recipes: List<FavoritesEntity>)
}
