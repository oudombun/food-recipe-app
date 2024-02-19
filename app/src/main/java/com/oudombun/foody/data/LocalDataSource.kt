package com.oudombun.foody.data

import com.oudombun.foody.data.database.entities.FavoritesEntity
import com.oudombun.foody.data.database.RecipesDao
import com.oudombun.foody.data.database.entities.FoodJokeEntity
import com.oudombun.foody.data.database.entities.RecipesEntity
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

    suspend fun insertFoodJoke(foodJokeEntity: FoodJokeEntity){
        dao.insertFoodJoke(foodJokeEntity)
    }

    fun readFoodJoke():Flow<List<FoodJokeEntity>>{
        return dao.readFoodJoke()
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