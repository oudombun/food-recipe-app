package com.oudombun.foody.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.oudombun.foody.models.FoodRecipe
import com.oudombun.foody.util.Constants.Companion.RECIPES_TABLE


@Entity(tableName = RECIPES_TABLE)
class RecipesEntity(
    var recipe: FoodRecipe
) {

    @PrimaryKey(autoGenerate = false)
    var id:Int=0
}