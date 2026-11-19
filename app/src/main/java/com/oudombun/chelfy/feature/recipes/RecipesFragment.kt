package com.oudombun.chelfy.feature.recipes

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.oudombun.chelfy.R
import com.oudombun.chelfy.databinding.FragmentRecipesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment : Fragment(R.layout.fragment_recipes) {
    private val viewModel: RecipesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentRecipesBinding.bind(view)
        val recipesAdapter = RecipesAdapter()
        val popularAdapter = FilterRecipesAdapter()
        val skeletonRecipes = SkeletonRecipesAdapter(5)
        val skeletonPopular = SkeletonFilterAdapter(5)
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPopularFood.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.searchViewBtn.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.recipesFragment) {
                findNavController().navigate(R.id.action_recipesFragment_to_searchFragment)
            }
        }
        binding.errorTextView.setOnClickListener { viewModel.refresh() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    val loading = state.isLoading && state.recipes.resultData.isEmpty()
                    binding.recyclerview.adapter = if (loading) skeletonRecipes else recipesAdapter
                    binding.rvPopularFood.adapter = if (loading) skeletonPopular else popularAdapter
                    binding.title.isVisible = !loading
                    binding.titlePopular.isVisible = !loading
                    recipesAdapter.setData(state.recipes)
                    popularAdapter.setData(state.popularRecipes)
                    val showError = !loading && state.error != null && state.recipes.resultData.isEmpty()
                    binding.errorImageView.isVisible = showError
                    binding.errorTextView.isVisible = showError
                    binding.errorTextView.text = state.error
                }
            }
        }
    }
}
