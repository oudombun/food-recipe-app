package com.oudombun.chelfy.data

import com.oudombun.chelfy.data.database.entities.FavoritesEntity
import com.oudombun.chelfy.data.database.RecipesDao
import com.oudombun.chelfy.data.database.entities.MealPlanEntity
import com.oudombun.chelfy.data.database.entities.RecipesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val dao: RecipesDao
) {

    suspend fun insertRecipes(recipesEntity: RecipesEntity){
        dao.insertRecipes(recipesEntity)
    }

    fun readDatabase(): Flow<List<RecipesEntity>> {
        return dao.readRecipes()
    }

    suspend fun insertMealPlan(mealPlanEntity: MealPlanEntity){
        dao.insertMealPlan(mealPlanEntity)
    }

    fun readMealPlan():Flow<List<MealPlanEntity>>{
        return dao.readMealPlan()
    }

    suspend fun insertFavoriteRecipes(favoritesEntity: FavoritesEntity) {
        dao.insertFavoriteRecipe(favoritesEntity)
    }

    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>> {
        return dao.readFavoriteRecipes()
    }

    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) {
        dao.deleteFavoriteRecipe(favoritesEntity)
    }

    suspend fun deleteAllFavoriteRecipes() {
        dao.deleteAllFavoriteRecipes()
    }
}