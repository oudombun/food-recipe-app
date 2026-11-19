package com.oudombun.chelfy.feature.mealplan

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oudombun.chelfy.core.common.NetworkResult
import com.oudombun.chelfy.core.model.MealPlanResponse
import com.oudombun.chelfy.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MealPlanUiState(
    val plan: MealPlanResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val cooldownSeconds: Long = 0
)

@HiltViewModel
class MealPlanViewModel @Inject constructor(
    private val repository: RecipeRepository,
    private val savedState: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(MealPlanUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.mealPlan.collect { rows ->
                rows.firstOrNull()?.mealPlan?.let { plan ->
                    _uiState.update { it.copy(plan = plan) }
                }
            }
        }
        if ((savedState.get<Long>(COOLDOWN_UNTIL) ?: 0) > 0) trackCooldown()
    }

    fun generate(diet: String, calories: Int?) {
        if (_uiState.value.isLoading || _uiState.value.cooldownSeconds > 0) return
        val count = savedState.get<Int>(REQUEST_COUNT) ?: 0
        if (count >= 3) {
            savedState[COOLDOWN_UNTIL] = System.currentTimeMillis() + 30 * 60 * 1_000L
            trackCooldown()
            return
        }
        savedState[REQUEST_COUNT] = count + 1
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = repository.generateMealPlan(diet, calories)) {
                is NetworkResult.Success -> _uiState.update {
                    it.copy(plan = result.data, isLoading = false)
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    private fun trackCooldown() {
        viewModelScope.launch {
            val until = savedState.get<Long>(COOLDOWN_UNTIL) ?: 0
            while (true) {
                val remaining = ((until - System.currentTimeMillis() + 999) / 1_000).coerceAtLeast(0)
                _uiState.update { it.copy(cooldownSeconds = remaining) }
                if (remaining == 0L) break
                delay(1_000)
            }
            savedState[REQUEST_COUNT] = 0
            savedState[COOLDOWN_UNTIL] = 0L
        }
    }

    private companion object {
        const val REQUEST_COUNT = "request_count"
        const val COOLDOWN_UNTIL = "cooldown_until"
    }
}
