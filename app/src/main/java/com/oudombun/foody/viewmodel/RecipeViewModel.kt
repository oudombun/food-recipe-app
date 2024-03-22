package com.oudombun.foody.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.oudombun.foody.BuildConfig.API_KEY2
import com.oudombun.foody.BuildConfig.API_KEY3
import com.oudombun.foody.data.DataStoreRepository
import com.oudombun.foody.util.Constants.Companion.API_KEY
import com.oudombun.foody.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.oudombun.foody.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.oudombun.foody.util.Constants.Companion.DEFAULT_RECIPES_NUMBER
import com.oudombun.foody.util.Constants.Companion.DESSERT_MEAL_TYPE
import com.oudombun.foody.util.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.oudombun.foody.util.Constants.Companion.QUERY_API_KEY
import com.oudombun.foody.util.Constants.Companion.QUERY_DIET
import com.oudombun.foody.util.Constants.Companion.QUERY_FILLS_INGREDIENTS
import com.oudombun.foody.util.Constants.Companion.QUERY_NUMBER
import com.oudombun.foody.util.Constants.Companion.QUERY_SEARCH
import com.oudombun.foody.util.Constants.Companion.QUERY_TYPE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RecipesViewModel @ViewModelInject constructor(
    application: Application,
    private val dataStoreRepository: DataStoreRepository
) : AndroidViewModel(application) {

    private var currentApiKeyIndex = 0
    private val apiKeys = listOf(API_KEY, API_KEY2, API_KEY3)

    private var mealType = DEFAULT_MEAL_TYPE
    private var dietType = DEFAULT_DIET_TYPE

    var networkStatus = false
    var backOnline = false

    val readMealAndDietType = dataStoreRepository.readMealAndDietType
    val readBackOnline = dataStoreRepository.readBackOnline.asLiveData()

    private fun chooseApiKey(): String {
        val apiKey = apiKeys[currentApiKeyIndex]
        currentApiKeyIndex = (currentApiKeyIndex + 1) % apiKeys.size // Increment and wrap around
        return apiKey
    }

    fun saveMealAndDietType(mealType: String, mealTypeId: Int, dietType: String, dietTypeId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveMealAndDietType(mealType, mealTypeId, dietType, dietTypeId)
        }

    private fun saveBackOnline(backOnline: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveBackOnline(backOnline)
        }

    fun applyQueries(isDesert:Boolean): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()

        viewModelScope.launch {
            readMealAndDietType.collect { value ->
                mealType = value.selectedMealType
                dietType = value.selectedDietType
            }
        }

        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_TYPE] =if(isDesert) DESSERT_MEAL_TYPE else  mealType
        queries[QUERY_DIET] = dietType
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILLS_INGREDIENTS] = "true"

        return queries
    }

//    fun applySearchQuery(searchQuery: String): HashMap<String, String> {
//        val queries: HashMap<String, String> = HashMap()
//        queries[QUERY_SEARCH] = searchQuery
//        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
//        queries[QUERY_API_KEY] = API_KEY
//        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
//        queries[QUERY_FILLS_INGREDIENTS] = "true"
//        return queries
//    }
    fun applySearchQuery(searchQuery: String,
                         maxResults: Int = 20,
                         fillIngredients: Boolean = false,
                         addRecipeInfo: Boolean = false): HashMap<String, String> {
    val apiKey = chooseApiKey()
    val queries: HashMap<String, String> = HashMap()
        queries[QUERY_SEARCH] = searchQuery
        queries[QUERY_NUMBER] = maxResults.toString() // Limit results
        queries[QUERY_API_KEY] = apiKey
        if (fillIngredients) {
            queries[QUERY_FILLS_INGREDIENTS] = "true"
        }

        if (addRecipeInfo) {
            queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        }

        return queries
    }


    fun showNetworkStatus() {
        if (!networkStatus) {
            Toast.makeText(getApplication(), "No Internet Connection.", Toast.LENGTH_SHORT).show()
            saveBackOnline(true)
        } else if (networkStatus) {
            if (backOnline) {
                Toast.makeText(getApplication(), "We're back online.", Toast.LENGTH_SHORT).show()
                saveBackOnline(false)
            }
        }
    }

}