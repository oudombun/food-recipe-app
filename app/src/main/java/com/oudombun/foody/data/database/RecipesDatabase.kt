package com.oudombun.foody.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.oudombun.foody.data.database.entities.FavoritesEntity
import com.oudombun.foody.data.database.entities.FoodJokeEntity
import com.oudombun.foody.data.database.entities.RecipesDessertEntity
import com.oudombun.foody.data.database.entities.RecipesEntity

@Database(
    entities = [RecipesEntity::class, FavoritesEntity::class,FoodJokeEntity::class,RecipesDessertEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RecipeTypeConverter::class)
abstract class RecipesDatabase : RoomDatabase() {
    abstract fun recipesDao(): RecipesDao
}