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
import com.oudombun.chelfy.databinding.FragmentSearchResultBinding
import com.oudombun.chelfy.util.NetworkResult
import com.oudombun.chelfy.viewmodel.MainViewModel
import com.oudombun.chelfy.viewmodel.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchResultFragment : Fragment() {

    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<SearchResultFragmentArgs>()
    private val mAdapter by lazy { GridAdapter() }

    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel

    private var mealType = ""
    private var dietType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        recipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchQuery = args.searchQuery.toString()

        // Recyclerview (normal)
        binding.recyclerView.adapter = mAdapter
        binding.recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        // Show the keyword chip/tag
        displaySearchQuery(searchQuery)

        // Listen when coming back from bottom sheet
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>("backFromSheet")
            ?.observe(viewLifecycleOwner) {
                searchApiData(searchQuery)
            }

        // Meal/Diet chips
        lifecycleScope.launch {
            recipesViewModel.readMealAndDietType.collect { value ->
                mealType = value.selectedMealType
                dietType = value.selectedDietType

                if (dietType.isNotEmpty() && mealType.isNotEmpty()) {
                    binding.txtMealType.text = mealType
                    binding.txtDietType.text = dietType
                }
            }
        }

        // Initial search
        searchApiData(searchQuery)

        binding.toolbarBackResult.setOnClickListener {
            findNavController().navigate(R.id.action_searchResultFragment_to_recipesFragment)
        }

        binding.searchButtonFilter.setOnClickListener {
            if (recipesViewModel.networkStatus) {
                val action =
                    SearchResultFragmentDirections.actionSearchResultFragmentToRecipesBottomSheet()
                findNavController().navigate(action)
            } else {
                recipesViewModel.showNetworkStatus()
            }
        }
    }

    private fun displaySearchQuery(query: String) {
        val inflater = LayoutInflater.from(requireContext())
        val queryTag = inflater.inflate(
            R.layout.layout_query_tag,
            binding.searchQueryContainer,
            false
        ) as LinearLayout

        queryTag.findViewById<TextView>(R.id.queryText).text = query
        queryTag.findViewById<ImageView>(R.id.removeQueryButton).setOnClickListener {
            binding.searchQueryContainer.removeView(queryTag)
            searchApiData(query) // re-run search if you want
        }

        binding.searchQueryContainer.addView(queryTag)
    }

    private fun searchApiData(searchQuery: String) {
        binding.searchButtonFilter.visibility = View.GONE
        setLoading(true)

        lifecycleScope.launch{
            mainViewModel.searchRecipes(recipesViewModel.applySearchQuery(searchQuery))
        }


        mainViewModel.searchedRecipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.searchButtonFilter.visibility = View.VISIBLE
                    setLoading(false)
                    response.data?.let { mAdapter.setData(it) }
                }

                is NetworkResult.Error -> {
                    setLoading(false)
                    Toast.makeText(requireContext(), response.message.toString(), Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.Loading -> {
                    setLoading(true)
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
