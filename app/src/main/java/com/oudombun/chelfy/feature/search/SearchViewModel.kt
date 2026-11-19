package com.oudombun.chelfy.feature.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oudombun.chelfy.core.common.NetworkResult
import com.oudombun.chelfy.core.model.FoodRecipe
import com.oudombun.chelfy.data.preferences.MealAndDietType
import com.oudombun.chelfy.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SearchUiState(
    val isLoading: Boolean = true,
    val recipes: FoodRecipe = FoodRecipe(emptyList()),
    val filters: MealAndDietType? = null,
    val error: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    repository: RecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val query = savedStateHandle.get<String>("search_query").orEmpty()

    val uiState = repository.filters.distinctUntilChanged().transformLatest { filters ->
        emit(SearchUiState(filters = filters))
        val state = when (val result = repository.search(query, filters)) {
            is NetworkResult.Success -> SearchUiState(false, requireNotNull(result.data), filters)
            is NetworkResult.Error -> SearchUiState(isLoading = false, filters = filters, error = result.message)
            is NetworkResult.Loading -> SearchUiState(filters = filters)
        }
        emit(state)
    }.stateIn(viewModelScope, SharingStarted.Lazily, SearchUiState())
}
