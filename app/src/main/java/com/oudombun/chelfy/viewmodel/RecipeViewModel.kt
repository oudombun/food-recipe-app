package com.oudombun.chelfy.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.oudombun.chelfy.BuildConfig.API_KEY2
import com.oudombun.chelfy.BuildConfig.API_KEY3
import com.oudombun.chelfy.data.DataStoreRepository
import com.oudombun.chelfy.util.Constants.Companion.API_KEY
import com.oudombun.chelfy.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.oudombun.chelfy.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.oudombun.chelfy.util.Constants.Companion.DEFAULT_RECIPES_NUMBER
import com.oudombun.chelfy.util.Constants.Companion.DEFAULT_RECIPES_SEARCH_NUMBER
import com.oudombun.chelfy.util.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.oudombun.chelfy.util.Constants.Companion.QUERY_API_KEY
import com.oudombun.chelfy.util.Constants.Companion.QUERY_DIET
import com.oudombun.chelfy.util.Constants.Companion.QUERY_NUMBER
import com.oudombun.chelfy.util.Constants.Companion.QUERY_SEARCH
import com.oudombun.chelfy.util.Constants.Companion.QUERY_TYPE
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

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
        currentApiKeyIndex = (currentApiKeyIndex + 1) % apiKeys.size
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

    /**
     * IMPORTANT FIX:
     * Old code used collect{} in a launch which is async (might not update before returning).
     * Here we use .first() so we get the latest value immediately before building the query map.
     */
    suspend fun applyQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()
        val apiKey = chooseApiKey()

        val value = readMealAndDietType.first()
        mealType = value.selectedMealType
        dietType = value.selectedDietType

        queries[QUERY_API_KEY] = apiKey
        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_TYPE] = mealType
        queries[QUERY_DIET] = dietType
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        return queries
    }

    suspend fun applySearchQuery(searchQuery: String): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()
        val apiKey = chooseApiKey()

        val value = readMealAndDietType.first()
        mealType = value.selectedMealType
        dietType = value.selectedDietType

        queries[QUERY_API_KEY] = apiKey
        queries[QUERY_SEARCH] = searchQuery

        // use your search number
        queries[QUERY_NUMBER] = DEFAULT_RECIPES_SEARCH_NUMBER

        // keep filters like before
        queries[QUERY_TYPE] = mealType
        queries[QUERY_DIET] = dietType
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        return queries
    }

    fun showNetworkStatus() {
        if (!networkStatus) {
            Toast.makeText(appContext, "No Internet Connection.", Toast.LENGTH_SHORT).show()
            saveBackOnline(true)
        } else {
            if (backOnline) {
                Toast.makeText(appContext, "We're back online.", Toast.LENGTH_SHORT).show()
                saveBackOnline(false)
            }
        }
    }
}
