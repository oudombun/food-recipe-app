package com.oudombun.chelfy.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.oudombun.chelfy.data.local.database.entities.FavoritesEntity
import com.oudombun.chelfy.data.local.database.entities.MealPlanEntity
import com.oudombun.chelfy.data.local.database.entities.RecipesEntity

@Database(
    entities = [RecipesEntity::class, FavoritesEntity::class,MealPlanEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RecipeTypeConverter::class)
abstract class RecipesDatabase : RoomDatabase() {
    abstract fun recipesDao(): RecipesDao
}