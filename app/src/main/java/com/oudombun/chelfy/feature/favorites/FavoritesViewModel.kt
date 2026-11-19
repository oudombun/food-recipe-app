package com.oudombun.chelfy.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oudombun.chelfy.data.local.database.entities.FavoritesEntity
import com.oudombun.chelfy.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {
    val favorites = repository.favorites.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )

    fun deleteFavorites(recipes: List<FavoritesEntity>) = viewModelScope.launch {
        repository.deleteFavorites(recipes)
    }
}
