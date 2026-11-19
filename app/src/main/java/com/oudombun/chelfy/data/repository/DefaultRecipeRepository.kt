package com.oudombun.chelfy.data.repository

import androidx.room.withTransaction
import com.google.gson.JsonParseException
import com.oudombun.chelfy.core.common.Constants
import com.oudombun.chelfy.core.common.NetworkResult
import com.oudombun.chelfy.core.model.AnalyzedInstruction
import com.oudombun.chelfy.core.model.FoodRecipe
import com.oudombun.chelfy.core.model.MealPlanResponse
import com.oudombun.chelfy.core.model.ResultData
import com.oudombun.chelfy.data.local.database.RecipesDatabase
import com.oudombun.chelfy.data.local.database.entities.FavoritesEntity
import com.oudombun.chelfy.data.local.database.entities.MealPlanEntity
import com.oudombun.chelfy.data.local.database.entities.RecipesEntity
import com.oudombun.chelfy.data.preferences.DataStoreRepository
import com.oudombun.chelfy.data.preferences.MealAndDietType
import com.oudombun.chelfy.data.remote.ApiKeyProvider
import com.oudombun.chelfy.data.remote.RemoteDataSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultRecipeRepository @Inject constructor(
    private val remote: RemoteDataSource,
    private val database: RecipesDatabase,
    private val preferences: DataStoreRepository,
    private val apiKeys: ApiKeyProvider
) : RecipeRepository {
    private val dao get() = database.recipesDao()
    override val recipes = dao.readRecipes()
    override val favorites = dao.readFavoriteRecipes()
    override val mealPlan = dao.readMealPlan()
    override val filters = preferences.readMealAndDietType

    override suspend fun getRecipes(): NetworkResult<FoodRecipe> {
        val result = request { remote.getRecipes(queries(filters.first())) }
        if (result is NetworkResult.Success) dao.insertRecipes(RecipesEntity(requireNotNull(result.data)))
        return result
    }

    override suspend fun search(query: String, filters: MealAndDietType): NetworkResult<FoodRecipe> =
        request {
            val queries = queries(filters).toMutableMap().apply {
                put(Constants.QUERY_SEARCH, query)
                put(Constants.QUERY_NUMBER, Constants.DEFAULT_RECIPES_SEARCH_NUMBER)
            }
            remote.searchRecipes(queries)
        }

    override suspend fun getInstructions(id: Int): NetworkResult<List<AnalyzedInstruction>> =
        request { remote.getAnalyzedInstructions(id.toString(), apiKeys.next()) }

    override suspend fun generateMealPlan(diet: String, calories: Int?): NetworkResult<MealPlanResponse> {
        val result = request { remote.getMealPlan(apiKeys.next(), diet = diet, targetCalories = calories) }
        if (result is NetworkResult.Success) dao.insertMealPlan(MealPlanEntity(requireNotNull(result.data)))
        return result
    }

    override suspend fun setFavorite(recipe: ResultData, saved: Boolean) {
        database.withTransaction {
            val matches = dao.getFavorites().filter { it.result.id == recipe.id }
            if (saved && matches.isEmpty()) dao.insertFavoriteRecipe(FavoritesEntity(0, recipe))
            if (!saved) matches.forEach { dao.deleteFavoriteRecipe(it) }
        }
    }

    override suspend fun deleteFavorites(recipes: List<FavoritesEntity>) {
        database.withTransaction {
            recipes.forEach { dao.deleteFavoriteRecipe(it) }
        }
    }

    private fun queries(filters: MealAndDietType): Map<String, String> = mapOf(
        Constants.QUERY_API_KEY to apiKeys.next(),
        Constants.QUERY_NUMBER to Constants.DEFAULT_RECIPES_NUMBER,
        Constants.QUERY_TYPE to filters.selectedMealType,
        Constants.QUERY_DIET to filters.selectedDietType,
        Constants.QUERY_ADD_RECIPE_INFORMATION to "true"
    )

    private suspend fun <T> request(call: suspend () -> Response<T>): NetworkResult<T> =
        try {
            val response = call()
            val body = response.body()
            when {
                response.code() == 402 -> NetworkResult.Error("Recipe API quota reached. Try again later.")
                response.isSuccessful && body != null -> NetworkResult.Success(body)
                response.isSuccessful -> NetworkResult.Error("The server returned an empty response.")
                else -> NetworkResult.Error("Couldn’t load recipes (HTTP ${response.code()}).")
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: SocketTimeoutException) {
            NetworkResult.Error("The request timed out. Try again.")
        } catch (exception: IOException) {
            NetworkResult.Error("Couldn’t connect. Check your internet connection.")
        } catch (exception: JsonParseException) {
            NetworkResult.Error("The server returned an unreadable recipe.")
        }
}
