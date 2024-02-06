package com.oudombun.foody.data

import com.oudombun.foody.data.database.RecipesDao
import com.oudombun.foody.data.database.RecipesEntity
import com.oudombun.foody.data.network.FoodRecipesApi
import com.oudombun.foody.models.FoodRecipe
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val dao: RecipesDao
) {

    suspend fun insertRecipes(recipesEntity: RecipesEntity){
        dao.insertRecipes(recipesEntity)
    }
    fun readDatabase(): Flow<List<RecipesEntity>> {
        return dao.readRecipes()
    }
}