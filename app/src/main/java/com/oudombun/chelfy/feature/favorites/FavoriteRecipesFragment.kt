package com.oudombun.chelfy.feature.favorites

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.oudombun.chelfy.R
import com.oudombun.chelfy.databinding.FragmentFavoriteRecipesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteRecipesFragment : Fragment(R.layout.fragment_favorite_recipes) {
    private val viewModel: FavoritesViewModel by viewModels()
    private var adapter: FavoriteRecipesAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentFavoriteRecipesBinding.bind(view)
        val recipesAdapter = FavoriteRecipesAdapter(requireActivity(), viewModel::deleteFavorites)
        adapter = recipesAdapter
        binding.favoriteRecipesRecyclerView.adapter = recipesAdapter
        binding.favoriteRecipesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favorites.collect { favorites ->
                    recipesAdapter.setData(favorites)
                    binding.favoriteRecipesRecyclerView.isVisible = favorites.isNotEmpty()
                    binding.noDataImageView.isVisible = favorites.isEmpty()
                    binding.noDataTextView.isVisible = favorites.isEmpty()
                }
            }
        }
    }

    override fun onDestroyView() {
        adapter?.clearContextualActionMode()
        view?.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.favoriteRecipesRecyclerView)?.adapter = null
        adapter = null
        super.onDestroyView()
    }
}
