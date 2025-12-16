package com.oudombun.chelfy.data.database

import androidx.annotation.Keep
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.oudombun.chelfy.models.FoodRecipe
import com.oudombun.chelfy.models.MealPlanResponse
import com.oudombun.chelfy.models.ResultData

@Keep
class RecipeTypeConverter {

    val gson = Gson()
    @TypeConverter
    fun foodRecipeToString(foodRecipe: FoodRecipe):String{
        return gson.toJson(foodRecipe)
    }
    @TypeConverter
    fun stringToFoodRecipe(data: String):FoodRecipe{
        val listType = object : TypeToken<FoodRecipe>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun resultDataToString(result: ResultData):String{
        return gson.toJson(result)
    }

    @TypeConverter
    fun stringToResultData(data: String):ResultData{
        val listType = object : TypeToken<ResultData>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun mealPlanToString(mealPlan: MealPlanResponse):String{
        return gson.toJson(mealPlan)
    }
    @TypeConverter
    fun stringToMealPlan(data: String):MealPlanResponse{
        val listType = object : TypeToken<MealPlanResponse>() {}.type
        return gson.fromJson(data, listType)
    }
}