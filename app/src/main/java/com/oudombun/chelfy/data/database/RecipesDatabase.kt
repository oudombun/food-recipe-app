package com.oudombun.chelfy.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.oudombun.chelfy.data.database.entities.FavoritesEntity
import com.oudombun.chelfy.data.database.entities.MealPlanEntity
import com.oudombun.chelfy.data.database.entities.RecipesEntity

@Database(
    entities = [RecipesEntity::class, FavoritesEntity::class,MealPlanEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RecipeTypeConverter::class)
abstract class RecipesDatabase : RoomDatabase() {
    abstract fun recipesDao(): RecipesDao
}