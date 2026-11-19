package com.oudombun.chelfy.feature.filters

import androidx.lifecycle.ViewModel
import com.oudombun.chelfy.data.preferences.DataStoreRepository
import com.oudombun.chelfy.data.preferences.MealAndDietType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FiltersViewModel @Inject constructor(
    private val repository: DataStoreRepository
) : ViewModel() {
    val filters = repository.readMealAndDietType

    suspend fun save(filters: MealAndDietType) {
        repository.saveMealAndDietType(
            filters.selectedMealType, filters.selectedMealTypeId,
            filters.selectedDietType, filters.selectedDietTypeId
        )
    }
}
