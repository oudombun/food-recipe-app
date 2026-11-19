package com.oudombun.chelfy.feature.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oudombun.chelfy.core.common.NetworkResult
import com.oudombun.chelfy.core.model.FoodRecipe
import com.oudombun.chelfy.data.remote.NetworkMonitor
import com.oudombun.chelfy.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecipesUiState(
    val isLoading: Boolean = true,
    val recipes: FoodRecipe = FoodRecipe(emptyList()),
    val popularRecipes: FoodRecipe = FoodRecipe(emptyList()),
    val error: String? = null
)

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val repository: RecipeRepository,
    networkMonitor: NetworkMonitor
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecipesUiState())
    val uiState = _uiState.asStateFlow()
    private var requestRunning = false

    init {
        viewModelScope.launch {
            val cached = repository.recipes.first().firstOrNull()?.foodRecipe
            if (cached != null) showRecipes(cached)
            networkMonitor.isOnline.collect { online ->
                if (online && _uiState.value.recipes.resultData.isEmpty()) refresh()
                if (!online && _uiState.value.recipes.resultData.isEmpty()) {
                    _uiState.update { it.copy(isLoading = false, error = "No internet connection.") }
                }
            }
        }
    }

    fun refresh() {
        if (requestRunning) return
        requestRunning = true
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                when (val result = repository.getRecipes()) {
                    is NetworkResult.Success -> showRecipes(requireNotNull(result.data))
                    is NetworkResult.Error -> _uiState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                    is NetworkResult.Loading -> Unit
                }
            } finally {
                requestRunning = false
            }
        }
    }

    private fun showRecipes(recipes: FoodRecipe) {
        val count = (recipes.resultData.size / 2).coerceAtLeast(1)
        _uiState.value = RecipesUiState(
            isLoading = false,
            recipes = FoodRecipe(recipes.resultData.shuffled().take(count)),
            popularRecipes = FoodRecipe(recipes.resultData.sortedByDescending { it.aggregateLikes }.take(count))
        )
    }
}
