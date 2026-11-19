package com.oudombun.chelfy.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.oudombun.chelfy.core.model.FoodRecipe
import com.oudombun.chelfy.core.common.Constants.Companion.RECIPES_TABLE


@Entity(tableName = RECIPES_TABLE)
class RecipesEntity(
    var foodRecipe: FoodRecipe
) {

    @PrimaryKey(autoGenerate = false)
    var id:Int=0
}