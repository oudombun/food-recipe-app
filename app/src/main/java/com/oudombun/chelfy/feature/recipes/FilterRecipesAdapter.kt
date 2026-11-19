package com.oudombun.chelfy.feature.recipes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.oudombun.chelfy.databinding.ItemFilterFoodCardBinding
import com.oudombun.chelfy.core.model.FoodRecipe
import com.oudombun.chelfy.core.model.ResultData
import com.oudombun.chelfy.core.common.RecipesDiffUtil

class FilterRecipesAdapter : RecyclerView.Adapter<FilterRecipesAdapter.MyViewHolder>() {

    private var recipes = emptyList<ResultData>()

    class MyViewHolder(private val binding: ItemFilterFoodCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(result: ResultData){
            binding.result = result
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemFilterFoodCardBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentRecipe = recipes[position]
        holder.bind(currentRecipe)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    fun setData(newData: FoodRecipe){
        val shuffledRecipes = newData.resultData.shuffled()
        val recipesDiffUtil = RecipesDiffUtil(recipes, shuffledRecipes)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        recipes = shuffledRecipes
        diffUtilResult.dispatchUpdatesTo(this)
    }
}