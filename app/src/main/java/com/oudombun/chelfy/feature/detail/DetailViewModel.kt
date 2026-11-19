package com.oudombun.chelfy.feature.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oudombun.chelfy.core.common.NetworkResult
import com.oudombun.chelfy.core.model.AnalyzedInstruction
import com.oudombun.chelfy.core.model.ResultData
import com.oudombun.chelfy.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {
    private val _instructions = MutableStateFlow<NetworkResult<List<AnalyzedInstruction>>>(NetworkResult.Loading())
    val instructions = _instructions.asStateFlow()
    val favorites = repository.favorites.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _messages = MutableSharedFlow<String>()
    val messages = _messages.asSharedFlow()
    private var recipeId: Int? = null
    private var instructionsJob: Job? = null
    private var favoriteJob: Job? = null

    fun loadInstructions(id: Int, refresh: Boolean = false) {
        if (!refresh && recipeId == id) return
        recipeId = id
        instructionsJob?.cancel()
        instructionsJob = viewModelScope.launch {
            _instructions.value = NetworkResult.Loading()
            _instructions.value = repository.getInstructions(id)
        }
    }

    fun setFavorite(recipe: ResultData, saved: Boolean) {
        if (favoriteJob?.isActive == true) return
        favoriteJob = viewModelScope.launch {
            try {
                repository.setFavorite(recipe, saved)
                _messages.emit(if (saved) "Recipe saved." else "Removed from Favorites.")
            } catch (exception: IOException) {
                _messages.emit("Couldn’t update favorites. Try again.")
            }
        }
    }
}
