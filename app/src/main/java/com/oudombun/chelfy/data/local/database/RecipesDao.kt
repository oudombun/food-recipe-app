package com.oudombun.chelfy.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.oudombun.chelfy.data.local.database.entities.FavoritesEntity
import com.oudombun.chelfy.data.local.database.entities.MealPlanEntity
import com.oudombun.chelfy.data.local.database.entities.RecipesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipesEntity: RecipesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealPlan(mealPlanEntity: MealPlanEntity)

    @Query("SELECT * FROM recipes_table ORDER BY id ASC")
    fun readRecipes(): Flow<List<RecipesEntity>>

    @Query("SELECT * FROM favorite_recipes_table ORDER BY id ASC")
    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>>


    @Query("SELECT * FROM meal_plan_table ORDER BY id ASC")
    fun readMealPlan(): Flow<List<MealPlanEntity>>

    @Query("SELECT * FROM favorite_recipes_table ORDER BY id ASC")
    suspend fun getFavorites(): List<FavoritesEntity>

    @Delete
    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity)

    @Query("DELETE FROM favorite_recipes_table")
    suspend fun deleteAllFavoriteRecipes()
}