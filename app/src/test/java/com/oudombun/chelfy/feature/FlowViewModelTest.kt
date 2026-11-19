package com.oudombun.chelfy.feature

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.oudombun.chelfy.core.common.NetworkResult
import com.oudombun.chelfy.core.model.AnalyzedInstruction
import com.oudombun.chelfy.core.model.FoodRecipe
import com.oudombun.chelfy.core.model.MealPlanResponse
import com.oudombun.chelfy.core.model.Nutrients
import com.oudombun.chelfy.core.model.ResultData
import com.oudombun.chelfy.data.local.database.entities.FavoritesEntity
import com.oudombun.chelfy.data.local.database.entities.MealPlanEntity
import com.oudombun.chelfy.data.local.database.entities.RecipesEntity
import com.oudombun.chelfy.data.preferences.MealAndDietType
import com.oudombun.chelfy.data.repository.RecipeRepository
import com.oudombun.chelfy.feature.detail.DetailViewModel
import com.oudombun.chelfy.feature.mealplan.MealPlanViewModel
import com.oudombun.chelfy.feature.search.SearchViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FlowViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun changingFiltersCancelsOldSearchAndKeepsNewResult() = runTest(dispatcher) {
        val repository = FakeRecipeRepository()
        val model = SearchViewModel(repository, SavedStateHandle(mapOf("search_query" to "rice")))
        val collector = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            model.uiState.collect {}
        }
        runCurrent()
        repository.filters.value = repository.filters.value.copy(selectedDietType = "vegan")
        runCurrent()
        advanceTimeBy(1_001)
        runCurrent()

        assertEquals(listOf("rice", "rice"), repository.searchQueries)
        assertEquals(1, repository.cancelledSearches)
        assertEquals("vegan", model.uiState.value.filters?.selectedDietType)
        assertFalse(model.uiState.value.isLoading)

        repository.filters.value = repository.filters.value.copy()
        runCurrent()
        assertEquals(2, repository.searchQueries.size)
        collector.cancel()
        model.viewModelScope.cancel()
    }

    @Test
    fun recreatingDetailViewDoesNotRefetchUntilExplicitRetry() = runTest(dispatcher) {
        val repository = FakeRecipeRepository()
        val model = DetailViewModel(repository)
        model.loadInstructions(12)
        runCurrent()
        model.loadInstructions(12)
        runCurrent()
        assertEquals(listOf(12), repository.instructionRequests)
        model.loadInstructions(12, refresh = true)
        runCurrent()
        assertEquals(listOf(12, 12), repository.instructionRequests)
        model.viewModelScope.cancel()
    }

    @Test
    fun cachedMealPlanIsAvailableBeforeGeneratingAndCaloriesReachRepository() = runTest(dispatcher) {
        val repository = FakeRecipeRepository()
        repository.mealPlan.value = listOf(MealPlanEntity(repository.plan))
        val model = MealPlanViewModel(repository, SavedStateHandle())
        runCurrent()
        assertEquals(repository.plan, model.uiState.value.plan)
        model.generate("Vegan", 1_800)
        model.generate("Vegan", 2_000)
        runCurrent()
        assertEquals(listOf(1_800), repository.calorieTargets)
        assertFalse(model.uiState.value.isLoading)
        model.viewModelScope.cancel()
    }

    @Test
    fun mealPlanCooldownResumesFromSavedStateAndReenablesGeneration() = runTest(dispatcher) {
        val repository = FakeRecipeRepository()
        val state = SavedStateHandle(
            mapOf("request_count" to 3, "cooldown_until" to System.currentTimeMillis() - 1)
        )
        val model = MealPlanViewModel(repository, state)
        runCurrent()
        assertEquals(0L, model.uiState.value.cooldownSeconds)
        model.generate("Vegan", null)
        runCurrent()
        assertEquals(listOf<Int?>(null), repository.calorieTargets)
        model.viewModelScope.cancel()
    }

    private class FakeRecipeRepository : RecipeRepository {
        override val recipes = MutableStateFlow(emptyList<RecipesEntity>())
        override val favorites = MutableStateFlow(emptyList<FavoritesEntity>())
        override val mealPlan = MutableStateFlow(emptyList<MealPlanEntity>())
        override val filters = MutableStateFlow(MealAndDietType("main course", 0, "gluten free", 0))
        val plan = MealPlanResponse(emptyList(), Nutrients(0.0, 0.0, 0.0, 0.0))
        val searchQueries = mutableListOf<String>()
        val instructionRequests = mutableListOf<Int>()
        val calorieTargets = mutableListOf<Int?>()
        var cancelledSearches = 0

        override suspend fun getRecipes() = NetworkResult.Success(FoodRecipe(emptyList()))

        override suspend fun search(query: String, filters: MealAndDietType): NetworkResult<FoodRecipe> {
            searchQueries.add(query)
            try {
                delay(1_000)
            } catch (exception: CancellationException) {
                cancelledSearches++
                throw exception
            }
            return NetworkResult.Success(FoodRecipe(emptyList()))
        }

        override suspend fun getInstructions(id: Int): NetworkResult<List<AnalyzedInstruction>> {
            instructionRequests.add(id)
            return NetworkResult.Success(emptyList())
        }

        override suspend fun generateMealPlan(diet: String, calories: Int?): NetworkResult<MealPlanResponse> {
            calorieTargets.add(calories)
            return NetworkResult.Success(plan)
        }

        override suspend fun setFavorite(recipe: ResultData, saved: Boolean) = Unit
        override suspend fun deleteFavorites(recipes: List<FavoritesEntity>) = Unit
    }
}
