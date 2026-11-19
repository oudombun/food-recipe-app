package com.oudombun.chelfy.data.local.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.oudombun.chelfy.core.model.FoodJoke
import com.oudombun.chelfy.core.model.MealPlanResponse
import com.oudombun.chelfy.core.common.Constants.Companion.MEAL_PLAN_TABLE

@Entity(tableName = MEAL_PLAN_TABLE)
class MealPlanEntity(
    var mealPlan: MealPlanResponse
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}