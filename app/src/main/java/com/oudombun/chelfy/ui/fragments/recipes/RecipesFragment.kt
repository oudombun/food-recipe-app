package com.oudombun.chelfy.ui.fragments.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.oudombun.chelfy.R
import com.oudombun.chelfy.adapters.FilterRecipesAdapter
import com.oudombun.chelfy.adapters.RecipesAdapter
import com.oudombun.chelfy.adapters.SkeletonFilterAdapter
import com.oudombun.chelfy.adapters.SkeletonRecipesAdapter
import com.oudombun.chelfy.databinding.FragmentRecipesBinding
import com.oudombun.chelfy.models.FoodRecipe
import com.oudombun.chelfy.util.NetworkListener
import com.oudombun.chelfy.util.NetworkResult
import com.oudombun.chelfy.util.observeOnce
import com.oudombun.chelfy.viewmodel.MainViewModel
import com.oudombun.chelfy.viewmodel.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RecipesFragment : Fragment() {

    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel

    // Real adapters
    private val mAdapter by lazy { RecipesAdapter() }
    private val mCircleAdapter by lazy { FilterRecipesAdapter() }

    // Skeleton adapters
    private val skeletonRecipesAdapter by lazy { SkeletonRecipesAdapter(5) }
    private val skeletonFilterAdapter by lazy { SkeletonFilterAdapter(5) }

    private lateinit var networkListener: NetworkListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        recipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.mainViewModel = mainViewModel

        setupRecyclerView()

        recipesViewModel.readBackOnline.observe(viewLifecycleOwner) {
            recipesViewModel.backOnline = it
        }

        lifecycleScope.launch {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext())
                .collect { status ->
                    recipesViewModel.networkStatus = status
                    recipesViewModel.showNetworkStatus()
                    readDatabase()
                }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchViewBtn.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.recipesFragment) {
                findNavController().navigate(R.id.action_recipesFragment_to_searchFragment)
            }
        }
    }

    private fun setupRecyclerView() {
        // Setup RecyclerView layout managers
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPopularFood.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Show skeleton loaders initially
        showSkeletonLoaders()
    }

    private fun showSkeletonLoaders() {
        binding.recyclerview.adapter = skeletonRecipesAdapter
        binding.rvPopularFood.adapter = skeletonFilterAdapter
        binding.title.isVisible = false
        binding.titlePopular.isVisible = false
    }

    private fun showRealData() {
        binding.recyclerview.adapter = mAdapter
        binding.rvPopularFood.adapter = mCircleAdapter
        binding.title.isVisible = true
        binding.titlePopular.isVisible = true
    }

    private fun readDatabase() {
        lifecycleScope.launch {
            mainViewModel.readRecipes.observeOnce(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
                    val foodRecipe = database[0].foodRecipe

                    val randomList =
                        foodRecipe.resultData.shuffled().take(foodRecipe.resultData.size / 2)
                    val popularList =
                        foodRecipe.resultData.sortedByDescending { it.aggregateLikes }
                            .take(foodRecipe.resultData.size / 2)

                    showRealData()
                    mAdapter.setData(FoodRecipe(randomList))
                    mCircleAdapter.setData(FoodRecipe(popularList))
                } else {
                    requestApiData()
                }
            }
        }
    }

    private fun requestApiData() {
        lifecycleScope.launch {
            val queries = recipesViewModel.applyQueries()
            mainViewModel.getRecipes(queries)
        }

        mainViewModel.recipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    showRealData()
                    response.data?.let { mAdapter.setData(it) }
                }

                is NetworkResult.Error -> {
                    showRealData()
                    loadDataFromCache()
                    Toast.makeText(requireContext(), response.message.toString(), Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.Loading -> {
                    showSkeletonLoaders()
                }
            }
        }

        mainViewModel.recipesPopularResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { mCircleAdapter.setData(it) }
                }

                is NetworkResult.Error -> {
                    loadDataFromCache()
                    Toast.makeText(requireContext(), response.message.toString(), Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.Loading -> {
                    // Skeleton already shown by main loading
                }
            }
        }
    }

    private fun loadDataFromCache() {
        lifecycleScope.launch {
            mainViewModel.readRecipes.observe(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
                    val foodRecipe = database[0].foodRecipe

                    val randomList =
                        foodRecipe.resultData.shuffled().take(foodRecipe.resultData.size / 2)
                    val popularList =
                        foodRecipe.resultData.sortedByDescending { it.aggregateLikes }
                            .take(foodRecipe.resultData.size / 2)

                    mAdapter.setData(FoodRecipe(randomList))
                    mCircleAdapter.setData(FoodRecipe(popularList))
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}