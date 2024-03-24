package com.oudombun.chelfy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.oudombun.chelfy.databinding.ItemGridBinding
import com.oudombun.chelfy.models.FoodRecipe
import com.oudombun.chelfy.models.ResultData
import com.oudombun.chelfy.util.RecipesDiffUtil

class GridAdapter :
    RecyclerView.Adapter<GridAdapter.ViewHolder>() {

    private var gridItems = emptyList<ResultData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentRecipe = gridItems[position]
        holder.bind(currentRecipe)
    }

    override fun getItemCount() = gridItems.size

    class ViewHolder(private val binding: ItemGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(result: ResultData){
            binding.result = result
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemGridBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }
    fun setData(newData: FoodRecipe){
        val recipesDiffUtil =
            RecipesDiffUtil(gridItems, newData.resultData)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        gridItems = newData.resultData
        diffUtilResult.dispatchUpdatesTo(this)
    }
}
