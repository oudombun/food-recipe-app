package com.oudombun.foody.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.oudombun.foody.models.FoodRecipe
import com.oudombun.foody.models.ResultData

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
}