package com.oudombun.chelfy.models

import com.google.gson.annotations.SerializedName

data class FoodRecipe(
    @SerializedName("results")
    val resultData: List<ResultData>
)