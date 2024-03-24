package com.oudombun.chelfy.ui.fragments.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.oudombun.chelfy.R
import com.oudombun.chelfy.adapters.FilterRecipesAdapter
import com.oudombun.chelfy.adapters.RecipesAdapter
import com.oudombun.chelfy.databinding.FragmentRecipesBinding
import com.oudombun.chelfy.models.FoodRecipe
import com.oudombun.chelfy.util.NetworkListener
import com.oudombun.chelfy.util.NetworkResult
import com.oudombun.chelfy.util.observeOnce
import com.oudombun.chelfy.viewmodel.MainViewModel
import com.oudombun.chelfy.viewmodel.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_recipes.searchViewBtn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RecipesFragment : Fragment() {
    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel
    private val mAdapter by lazy { RecipesAdapter() }
    private val mCircleAdapter by lazy { FilterRecipesAdapter() }

    private lateinit var networkListener: NetworkListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        recipesViewModel = ViewModelProvider(requireActivity()).get(RecipesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
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
        searchViewBtn.setOnClickListener {
            if(findNavController().currentDestination?.id == R.id.recipesFragment){
                findNavController().navigate(R.id.action_recipesFragment_to_searchFragment)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerview.adapter = mAdapter
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())

        binding.rvPopularFood.adapter = mCircleAdapter
        binding.rvPopularFood.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        showShimmerEffect()
        showShimmerPopularEffect()
    }

    private fun readDatabase() {
        lifecycleScope.launch {
            mainViewModel.readRecipes.observeOnce(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
                    val foodRecipe = database[0].foodRecipe
                    val randomList = foodRecipe.resultData.shuffled().take(foodRecipe.resultData.size / 2) // Random
                    val popularList = foodRecipe.resultData.sortedByDescending { it.aggregateLikes }.take(foodRecipe.resultData.size / 2) // Popular
                    mAdapter.setData(FoodRecipe(randomList))
                    mCircleAdapter.setData(FoodRecipe(popularList))
                    hideShimmerEffect()
                    hideShimmerPopularEffect()
                }else{
                    requestApiData()
                }
            }
        }
    }

    private fun requestApiData() {
        mainViewModel.getRecipes(recipesViewModel.applyQueries())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let {
                        mAdapter.setData(it)
                    }
                }

                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    loadDataFromCache()
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
        mainViewModel.recipesPopularResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerPopularEffect()
                    response.data?.let {
                        mCircleAdapter.setData(it)
                    }
                }

                is NetworkResult.Error -> {
                    hideShimmerPopularEffect()
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is NetworkResult.Loading -> {
                    showShimmerPopularEffect()
                }
            }
        }
    }

    private fun loadDataFromCache() {
        lifecycleScope.launch {
            mainViewModel.readRecipes.observe(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
                    val foodRecipe = database[0].foodRecipe
                    val randomList = foodRecipe.resultData.shuffled().take(foodRecipe.resultData.size / 2) // Random
                    val popularList = foodRecipe.resultData.sortedByDescending { it.aggregateLikes }.take(foodRecipe.resultData.size / 2) // Popular
                    mAdapter.setData(FoodRecipe(randomList))
                    mCircleAdapter.setData(FoodRecipe(popularList))
                }
            }
        }
    }

    private fun showShimmerEffect() {
        binding.recyclerview.showShimmer()
    }

    private fun hideShimmerEffect() {
        binding.recyclerview.hideShimmer()
    }
    private fun showShimmerPopularEffect() {
        binding.rvPopularFood.showShimmer()
    }

    private fun hideShimmerPopularEffect() {
        binding.rvPopularFood.hideShimmer()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun addFragment(fragment: Fragment, bundle: Bundle? = null) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        fragment.arguments = bundle
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.slide_in_top, R.anim.slide_out_bottom)
        transaction.add(R.id.navHostFragment, fragment).addToBackStack(fragment::class.java.name)
        transaction.commit()
    }



}