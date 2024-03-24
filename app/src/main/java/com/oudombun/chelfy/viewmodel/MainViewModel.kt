package com.oudombun.chelfy.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.oudombun.chelfy.BuildConfig
import com.oudombun.chelfy.data.Repository
import com.oudombun.chelfy.data.database.entities.FavoritesEntity
import com.oudombun.chelfy.data.database.entities.MealPlanEntity
import com.oudombun.chelfy.data.database.entities.RecipesEntity
import com.oudombun.chelfy.models.AnalyzedInstruction
import com.oudombun.chelfy.models.FoodJoke
import com.oudombun.chelfy.models.FoodRecipe
import com.oudombun.chelfy.models.MealPlanResponse
import com.oudombun.chelfy.util.Constants
import com.oudombun.chelfy.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception

class MainViewModel @ViewModelInject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {


    /** ROOM DATABASE */

    val readRecipes: LiveData<List<RecipesEntity>> = repository.local.readDatabase().asLiveData()
    val readFavoriteRecipes: LiveData<List<FavoritesEntity>> = repository.local.readFavoriteRecipes().asLiveData()
    val readMealPlan: LiveData<List<MealPlanEntity>> = repository.local.readMealPlan().asLiveData()
    private fun insertRecipes(recipesEntity: RecipesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertRecipes(recipesEntity)
        }
    fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertFavoriteRecipes(favoritesEntity)
        }
    private fun insertMealPlan(mealPlanEntity: MealPlanEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertMealPlan(mealPlanEntity)
        }

    fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteFavoriteRecipe(favoritesEntity)
        }


    /** RETROFIT */
    var recipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    var recipesPopularResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    var searchedRecipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    var recipesDetailResponse: MutableLiveData<NetworkResult<List<AnalyzedInstruction>>> = MutableLiveData()
    var mealPlanResponse: MutableLiveData<NetworkResult<MealPlanResponse>> = MutableLiveData()


    private var currentApiKeyIndex = 0
    private val apiKeys = listOf(Constants.API_KEY, BuildConfig.API_KEY2, BuildConfig.API_KEY3)
    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRecipesSafeCall(queries)
    }

    fun getDetailRecipe(id:String) = viewModelScope.launch {
        getDetailRecipeSafeCall(id)
    }

    fun searchRecipes(searchQuery: Map<String, String>) = viewModelScope.launch {
        searchRecipesSafeCall(searchQuery)
    }

    fun getMealPlan(diet: String, targetCalories: Int? = null) = viewModelScope.launch {
        generateMealPlan(diet,targetCalories)
    }

    private suspend fun generateMealPlan(diet: String, targetCalories: Int? = null){

        mealPlanResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val apiKey = chooseApiKey()
                val response = repository.remote.getMealPlan(apiKey, diet = diet, targetCalories = targetCalories)
                mealPlanResponse.value = handleMealPlanResponse(response)

                val mealPlan = mealPlanResponse.value!!.data
                if(mealPlan != null){
                    offlineCacheMealPlan(mealPlan)
                }
            } catch (e: Exception) {
                mealPlanResponse.value = NetworkResult.Error("Meal Plan not found.")
            }
        } else {
            mealPlanResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        recipesResponse.value = NetworkResult.Loading()
        recipesPopularResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getRecipes(queries)
                if(response.isSuccessful){
                    val foodRecipe = response.body()!!

                    val randomList = foodRecipe.resultData.shuffled().take(foodRecipe.resultData.size / 2) // Random
                    val popularList = foodRecipe.resultData.sortedByDescending { it.aggregateLikes }.take(foodRecipe.resultData.size / 2) // Popular

                    recipesResponse.value = NetworkResult.Success(FoodRecipe(randomList))
                    recipesPopularResponse.value = NetworkResult.Success(FoodRecipe(popularList))

                    // Cache the recipes
                    offlineCacheRecipes(foodRecipe)
                }else{
                    recipesResponse.value = handleFoodRecipesResponse(response)
                    recipesPopularResponse.value = handleFoodRecipesResponse(response)
                }
            } catch (e: Exception) {
                recipesResponse.value = NetworkResult.Error("Recipes not found.")
                recipesPopularResponse.value = NetworkResult.Error("Recipes not found.")
            }
        } else {
            recipesResponse.value = NetworkResult.Error("No Internet Connection.")
            recipesPopularResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private suspend fun getDetailRecipeSafeCall(recipeId:String) {
        recipesDetailResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val apiKey = chooseApiKey()
                val response = repository.remote.getAnalyzedInstructions(recipeId,apiKey)
                if(response.isSuccessful){
                    val foodRecipe = response.body()!!
                    recipesDetailResponse.value = NetworkResult.Success(foodRecipe)
                }else{
                    recipesDetailResponse.value = handleFoodRecipesDetailResponse(response)
                }
            } catch (e: Exception) {
                recipesDetailResponse.value = NetworkResult.Error("Recipes not found.")
            }
        } else {
            recipesDetailResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private suspend fun searchRecipesSafeCall(searchQuery: Map<String, String>) {
        searchedRecipesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.searchRecipes(searchQuery)
                if(response.isSuccessful){
                    searchedRecipesResponse.value = NetworkResult.Success(response.body()!!)
                }else{
                    searchedRecipesResponse.value = handleFoodRecipesResponse(response)
                }
            } catch (e: Exception) {
                searchedRecipesResponse.value = NetworkResult.Error("Recipes not found.")
            }
        } else {
            searchedRecipesResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }
    private fun offlineCacheMealPlan(mealPlan: MealPlanResponse) {
        val mealPlanEntity = MealPlanEntity(mealPlan)
        insertMealPlan(mealPlanEntity)
    }


    private fun offlineCacheRecipes(foodRecipe: FoodRecipe) {
        val recipesEntity = RecipesEntity(foodRecipe)
        insertRecipes(recipesEntity)
    }

    private fun handleFoodRecipesResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe> {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("API Key Limited.")
            }
            response.body()!!.resultData.isEmpty() -> {
                return NetworkResult.Error("Recipes not found.")
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }
    private fun handleFoodRecipesDetailResponse(response: Response<List<AnalyzedInstruction>>): NetworkResult<List<AnalyzedInstruction>> {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("API Key Limited.")
            }
            response.body()!!.isEmpty() -> {
                return NetworkResult.Error("Recipes not found.")
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }

    private fun handleFoodJokeResponse(response: Response<FoodJoke>): NetworkResult<FoodJoke>? {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                NetworkResult.Error("API Key Limited.")
            }
            response.isSuccessful -> {
                val foodJoke = response.body()
                NetworkResult.Success(foodJoke!!)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }
    private fun handleMealPlanResponse(response: Response<MealPlanResponse>): NetworkResult<MealPlanResponse>? {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                NetworkResult.Error("API Key Limited.")
            }
            response.isSuccessful -> {
                val mealPlan = response.body()
                NetworkResult.Success(mealPlan!!)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

    private fun chooseApiKey(): String {
        val apiKey = apiKeys[currentApiKeyIndex]
        currentApiKeyIndex = (currentApiKeyIndex + 1) % apiKeys.size // Increment and wrap around
        return apiKey
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

}