package com.oudombun.chelfy.feature.mealplan

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.oudombun.chelfy.R
import com.oudombun.chelfy.databinding.FragmentMealPlanBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MealPlanFragment : Fragment(R.layout.fragment_meal_plan) {
    private val viewModel: MealPlanViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentMealPlanBinding.bind(view)
        binding.generateButton.setOnClickListener {
            val input = binding.caloriesEdittext.text.toString().trim()
            val calories = input.toIntOrNull()
            if (input.isNotEmpty() && (calories == null || calories <= 0)) {
                binding.caloriesEdittext.error = getString(R.string.invalid_calories)
                return@setOnClickListener
            }
            viewModel.generate(binding.dietSpinner.selectedItem.toString(), calories)
        }
        listOf(binding.rowBreakfast, binding.rowLunch, binding.rowDinner).forEachIndexed { index, row ->
            row.setOnClickListener {
                val meal = viewModel.uiState.value.plan?.meals?.getOrNull(index) ?: return@setOnClickListener
                findNavController().navigate(
                    R.id.action_foodJokeFragment_to_mealPlanDetailFragment,
                    Bundle().apply { putString("sourceUrl", meal.sourceUrl) }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.mealPlan = state.plan
                    binding.layoutMeal.isVisible = state.plan != null
                    binding.progressBar.isVisible = state.isLoading
                    binding.generateButton.isEnabled = !state.isLoading && state.cooldownSeconds == 0L
                    binding.generateButton.text = if (state.cooldownSeconds > 0) {
                        getString(R.string.meal_plan_cooldown, state.cooldownSeconds / 60, state.cooldownSeconds % 60)
                    } else {
                        getString(R.string.generate_meal_plan)
                    }
                    listOf(binding.rowBreakfast, binding.rowLunch, binding.rowDinner).forEachIndexed { index, row ->
                        row.isVisible = state.plan?.meals?.getOrNull(index) != null
                    }
                    state.error?.let { Snackbar.make(view, it, Snackbar.LENGTH_LONG).show() }
                }
            }
        }
    }
}
