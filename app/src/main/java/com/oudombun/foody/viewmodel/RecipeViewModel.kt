package com.oudombun.foody.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.oudombun.foody.data.Repository
import com.oudombun.foody.models.FoodRecipe
import com.oudombun.foody.util.Constants
import com.oudombun.foody.util.Constants.Companion.API_KEY
import com.oudombun.foody.util.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.oudombun.foody.util.Constants.Companion.QUERY_API_KEY
import com.oudombun.foody.util.Constants.Companion.QUERY_DIET
import com.oudombun.foody.util.Constants.Companion.QUERY_FILLS_INGREDIENTS
import com.oudombun.foody.util.Constants.Companion.QUERY_NUMBER
import com.oudombun.foody.util.Constants.Companion.QUERY_TYPE
import com.oudombun.foody.util.NetworkResult
import kotlinx.coroutines.launch
import retrofit2.Response

class RecipeViewModel @ViewModelInject constructor(
    application: Application,
    private val repository: Repository
):AndroidViewModel(application) {

    fun applyQuery(): HashMap<String, String> {
        val queries:HashMap<String,String> = HashMap()
        queries[QUERY_NUMBER] = "50"
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_TYPE] = "snack"
        queries[QUERY_DIET] = "vegan"
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILLS_INGREDIENTS] = "true"
        return queries
    }

}