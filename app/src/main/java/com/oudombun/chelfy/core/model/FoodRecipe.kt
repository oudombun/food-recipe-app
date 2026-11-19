package com.oudombun.chelfy.core.model

import com.google.gson.annotations.SerializedName

data class FoodRecipe(
    @SerializedName("results")
    val resultData: List<ResultData>
)