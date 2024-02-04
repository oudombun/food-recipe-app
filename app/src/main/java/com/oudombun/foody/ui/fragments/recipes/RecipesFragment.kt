package com.oudombun.foody.ui.fragments.recipes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foody.R
import com.oudombun.foody.viewmodel.MainViewModel
import com.oudombun.foody.adapters.RecipeAdapter
import com.oudombun.foody.util.NetworkResult
import com.oudombun.foody.viewmodel.RecipeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_recipes.view.recyclerview

@AndroidEntryPoint
class RecipesFragment : Fragment() {
    private val adapter by lazy { RecipeAdapter() }
    private lateinit var mView:View
    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipeViewModel: RecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        recipeViewModel = ViewModelProvider(requireActivity()).get(RecipeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_recipes, container, false)

        setupRecyclerView()
        readDatabase()

        return mView
    }

    private fun requestApiData() {
        mainViewModel.getRecipe(recipeViewModel.applyQuery())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner){response->
            when(response){
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let { adapter.setData(it) }
                }
                is NetworkResult.Error ->{
                    hideShimmerEffect()
                    Toast.makeText(requireContext(),response.message,Toast.LENGTH_LONG).show()
                }
                is NetworkResult.Loading ->{
                    showShimmerEffect()
                }
            }
        }
    }

    private fun readDatabase(){
        mainViewModel.readRecipes.observe(viewLifecycleOwner){database->
            if(database.isNotEmpty()){
                adapter.setData(database[0].recipe)
                hideShimmerEffect()
            }else{
                requestApiData()
            }
        }
    }


    private fun setupRecyclerView(){
        mView.recyclerview.adapter = adapter
        mView.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    private fun showShimmerEffect(){
        mView.recyclerview.showShimmer()
    }
    private fun hideShimmerEffect(){
        mView.recyclerview.hideShimmer()
    }
}