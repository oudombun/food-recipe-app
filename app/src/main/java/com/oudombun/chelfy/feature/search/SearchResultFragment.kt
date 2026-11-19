package com.oudombun.chelfy.feature.search

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.oudombun.chelfy.R
import com.oudombun.chelfy.databinding.FragmentSearchResultBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchResultFragment : Fragment(R.layout.fragment_search_result) {
    private val args by navArgs<SearchResultFragmentArgs>()
    private val viewModel: SearchViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentSearchResultBinding.bind(view)
        val adapter = GridAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        val tag = layoutInflater.inflate(R.layout.layout_query_tag, binding.searchQueryContainer, false) as LinearLayout
        tag.findViewById<TextView>(R.id.queryText).text = args.searchQuery
        tag.findViewById<ImageView>(R.id.removeQueryButton).setOnClickListener {
            findNavController().popBackStack()
        }
        binding.searchQueryContainer.addView(tag)
        binding.toolbarBackResult.setOnClickListener { findNavController().popBackStack() }
        binding.searchButtonFilter.setOnClickListener {
            findNavController().navigate(SearchResultFragmentDirections.actionSearchResultFragmentToRecipesBottomSheet())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progress.isVisible = state.isLoading
                    binding.searchButtonFilter.isVisible = !state.isLoading
                    binding.txtMealType.text = state.filters?.selectedMealType
                    binding.txtDietType.text = state.filters?.selectedDietType
                    adapter.setData(state.recipes)
                    state.error?.let { Snackbar.make(view, it, Snackbar.LENGTH_LONG).show() }
                }
            }
        }
    }
}
