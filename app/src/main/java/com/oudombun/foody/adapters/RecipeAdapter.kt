package com.oudombun.foody.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.foody.databinding.RecipesRowLayoutBinding
import com.oudombun.foody.models.FoodRecipe
import com.oudombun.foody.models.Result
import com.oudombun.foody.util.RecipeDiffUtil

class RecipeAdapter : RecyclerView.Adapter<RecipeAdapter.MyViewHolder>() {
    private var recipeList = emptyList<Result>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeAdapter.MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecipeAdapter.MyViewHolder, position: Int) {
        val current = recipeList[position]
        holder.bind(current)
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    fun setData(foodRecipe: FoodRecipe){
        val recipeDiffUtil = RecipeDiffUtil(recipeList,foodRecipe.results)
        val recipeDiffResult = DiffUtil.calculateDiff(recipeDiffUtil)
        recipeList = foodRecipe.results
        recipeDiffResult.dispatchUpdatesTo(this)
    }

    class MyViewHolder(private val binding: RecipesRowLayoutBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(result: Result){
            binding.result = result
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent:ViewGroup): MyViewHolder{
                val inflater = LayoutInflater.from(parent.context)
                val binding = RecipesRowLayoutBinding.inflate(inflater,parent,false)
                return MyViewHolder(binding)
            }
        }
    }
}