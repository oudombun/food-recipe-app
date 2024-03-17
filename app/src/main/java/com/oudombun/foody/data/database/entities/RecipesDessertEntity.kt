package com.oudombun.foody.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.oudombun.foody.models.FoodRecipe
import com.oudombun.foody.util.Constants.Companion.RECIPES_TABLE
import com.oudombun.foody.util.Constants.Companion.RECIPES_TABLE_DESSERT


@Entity(tableName = RECIPES_TABLE_DESSERT)
class RecipesDessertEntity(
    var foodRecipe: FoodRecipe
) {

    @PrimaryKey(autoGenerate = false)
    var id:Int=0
}