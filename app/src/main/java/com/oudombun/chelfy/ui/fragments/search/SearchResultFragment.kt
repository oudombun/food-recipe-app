package com.oudombun.chelfy.ui.fragments.search


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.oudombun.chelfy.R
import com.oudombun.chelfy.adapters.GridAdapter
import com.oudombun.chelfy.util.NetworkResult
import com.oudombun.chelfy.viewmodel.MainViewModel
import com.oudombun.chelfy.viewmodel.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_search_result.searchButtonFilter
import kotlinx.android.synthetic.main.fragment_search_result.shimmer_recycler_view
import kotlinx.android.synthetic.main.fragment_search_result.toolbarBackResult
import kotlinx.android.synthetic.main.fragment_search_result.txtDietType
import kotlinx.android.synthetic.main.fragment_search_result.txtMealType
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchResultFragment : Fragment() {
    private val args by navArgs<SearchResultFragmentArgs>()
    private val mAdapter by lazy { GridAdapter() }
    private lateinit var searchQuery: String
    private lateinit var searchQueryContainer: LinearLayout
    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel
    var mealType =""
    var dietType =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchQuery = args.searchQuery.toString()
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


        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("backFromSheet")
            ?.observe(viewLifecycleOwner) {
                searchApiData(searchQuery)
            }

        val recyclerView: RecyclerView = view.findViewById(R.id.shimmer_recycler_view)
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        searchQueryContainer = view.findViewById(R.id.searchQueryContainer)
        displaySearchQuery(searchQuery)

        lifecycleScope.launch {
            recipesViewModel.readMealAndDietType.collect { value ->
                mealType = value.selectedMealType
                dietType = value.selectedDietType

                if(dietType.isNotEmpty()&&mealType.isNotEmpty()){
                    txtMealType.text = mealType
                    txtDietType.text = dietType
                }
            }
        }
        searchApiData(searchQuery)

        toolbarBackResult.setOnClickListener {
           findNavController().navigate(R.id.action_searchResultFragment_to_recipesFragment)
        }

        searchButtonFilter.setOnClickListener {
            if (recipesViewModel.networkStatus) {
                val action =
                    SearchResultFragmentDirections.actionSearchResultFragmentToRecipesBottomSheet()
                findNavController().navigate(action)
            } else {
                recipesViewModel.showNetworkStatus()
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }
    private fun displaySearchQuery(query: String) {
        val inflater = LayoutInflater.from(context)
        val queryTag = inflater.inflate(R.layout.layout_query_tag, searchQueryContainer, false) as LinearLayout

        queryTag.findViewById<TextView>(R.id.queryText).text = query

        queryTag.findViewById<ImageView>(R.id.removeQueryButton).setOnClickListener {
            searchQueryContainer.removeView(queryTag)
            searchApiData(searchQuery)
        }
        searchQueryContainer.addView(queryTag)
    }

    private fun searchApiData(searchQuery: String) {
        searchButtonFilter.visibility = View.GONE
        showShimmerEffect()
        mainViewModel.searchRecipes(recipesViewModel.applySearchQuery(searchQuery))
        mainViewModel.searchedRecipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    searchButtonFilter.visibility = View.VISIBLE
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