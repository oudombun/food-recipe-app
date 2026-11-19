package com.oudombun.chelfy.feature.filters

import android.os.Bundle
import android.view.View
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.oudombun.chelfy.R
import com.oudombun.chelfy.data.preferences.MealAndDietType
import com.oudombun.chelfy.databinding.RecipesBottomSheetBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale
import androidx.navigation.fragment.findNavController

@AndroidEntryPoint
class RecipesBottomSheet : BottomSheetDialogFragment(R.layout.recipes_bottom_sheet) {
    private val viewModel: FiltersViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = RecipesBottomSheetBinding.bind(view)
        binding.applyBtn.isEnabled = false
        viewLifecycleOwner.lifecycleScope.launch {
            val filters = viewModel.filters.first()
            // Match persisted names as resource IDs can change between app versions.
            selectChip(binding.mealTypeChipGroup, filters.selectedMealType)
            selectChip(binding.dietTypeChipGroup, filters.selectedDietType)
            binding.applyBtn.isEnabled = true
        }
        binding.applyBtn.setOnClickListener {
            val meal = binding.mealTypeChipGroup.findViewById<Chip>(binding.mealTypeChipGroup.checkedChipId)
            val diet = binding.dietTypeChipGroup.findViewById<Chip>(binding.dietTypeChipGroup.checkedChipId)
            if (meal == null || diet == null) return@setOnClickListener
            binding.applyBtn.isEnabled = false
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    viewModel.save(
                        MealAndDietType(
                            meal.text.toString().lowercase(Locale.ROOT), meal.id,
                            diet.text.toString().lowercase(Locale.ROOT), diet.id
                        )
                    )
                    findNavController().popBackStack()
                } catch (exception: IOException) {
                    binding.applyBtn.isEnabled = true
                    Snackbar.make(view, "Couldn’t save filters. Try again.", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun selectChip(group: ChipGroup, name: String) {
        group.children.filterIsInstance<Chip>()
            .firstOrNull { it.text.toString().equals(name, ignoreCase = true) }
            ?.let { group.check(it.id) }
    }
}
