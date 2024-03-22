package com.oudombun.foody.ui.fragments.search


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.oudombun.foody.R
import com.oudombun.foody.adapters.GridAdapter
import com.oudombun.foody.adapters.RecipesAdapter
import com.oudombun.foody.ui.fragments.recipes.RecipesFragmentArgs
import com.oudombun.foody.util.NetworkResult
import com.oudombun.foody.viewmodel.MainViewModel
import com.oudombun.foody.viewmodel.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_recipes.recyclerview
import kotlinx.android.synthetic.main.fragment_search.toolbarBack
import kotlinx.android.synthetic.main.fragment_search_result.searchButton
import kotlinx.android.synthetic.main.fragment_search_result.shimmer_recycler_view
import kotlinx.android.synthetic.main.fragment_search_result.toolbarBackResult


@AndroidEntryPoint
class SearchResultFragment : Fragment() {
    private val args by navArgs<SearchResultFragmentArgs>()
    private val mAdapter by lazy { GridAdapter() }
    private lateinit var searchQuery: String
    private lateinit var searchQueryContainer: LinearLayout
    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchQuery = arguments?.getString(ARG_SEARCH_QUERY) ?: ""
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        recipesViewModel = ViewModelProvider(requireActivity()).get(RecipesViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.shimmer_recycler_view)
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        searchQueryContainer = view.findViewById(R.id.searchQueryContainer)
        displaySearchQuery(searchQuery)
        searchApiData(searchQuery)

        toolbarBackResult.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            if (fragmentManager.backStackEntryCount > 0) {
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }

        searchButton.setOnClickListener {
            if (recipesViewModel.networkStatus) {
                findNavController().navigate(R.id.action_searchResultFragment_to_recipesBottomSheet)
            } else {
                recipesViewModel.showNetworkStatus()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(!args.backFromBottomSheet){

        }
    }
    private fun displaySearchQuery(query: String) {
        val inflater = LayoutInflater.from(context)
        val queryTag = inflater.inflate(R.layout.layout_query_tag, searchQueryContainer, false) as LinearLayout

        queryTag.findViewById<TextView>(R.id.queryText).text = query

        queryTag.findViewById<TextView>(R.id.removeQueryButton).setOnClickListener {
            searchQueryContainer.removeView(queryTag)
        }

        searchQueryContainer.addView(queryTag)
    }

    private fun searchApiData(searchQuery: String) {
        showShimmerEffect()
        mainViewModel.searchRecipes(recipesViewModel.applySearchQuery(searchQuery))
        mainViewModel.searchedRecipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    val foodRecipe = response.data
                    foodRecipe?.let {
                        mAdapter.setData(it)
                    }
                }

                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }
        }
    }

    private fun showShimmerEffect() {
        shimmer_recycler_view.showShimmerAdapter()
    }

    private fun hideShimmerEffect() {
        shimmer_recycler_view.hideShimmerAdapter()
    }
    companion object {
        private const val ARG_SEARCH_QUERY = "search_query"

        fun newInstance(query: String): SearchResultFragment {
            val fragment = SearchResultFragment()
            val args = Bundle().apply {
                putString(ARG_SEARCH_QUERY, query)
            }
            fragment.arguments = args
            return fragment
        }
    }
}