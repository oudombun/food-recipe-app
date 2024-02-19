package com.oudombun.foody.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.foody.R
import com.oudombun.foody.models.AnalyzedInstruction
import com.oudombun.foody.util.RecipesDiffUtil

class AnalyzedInstructionAdapter: RecyclerView.Adapter<AnalyzedInstructionAdapter.MyViewHolder>()  {
    private var ingredientsList = emptyList<AnalyzedInstruction>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.ingredients_row_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return ingredientsList.size
    }

    fun setData(newIngredients: List<AnalyzedInstruction>) {
        val ingredientsDiffUtil =
            RecipesDiffUtil(ingredientsList, newIngredients)
        val diffUtilResult = DiffUtil.calculateDiff(ingredientsDiffUtil)
        ingredientsList = newIngredients
        diffUtilResult.dispatchUpdatesTo(this)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val analyzedInstruction = ingredientsList[position]
        holder.bind(analyzedInstruction)
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val stepNumberTextView: TextView = itemView.findViewById(R.id.ingredient_name)

        fun bind(analyzedInstruction: AnalyzedInstruction) {
            // Display the step number (assuming there's only one step for simplicity)
            if (analyzedInstruction.steps.isNotEmpty()) {
                val stepNumber = analyzedInstruction.steps[0].number
                stepNumberTextView.text = "Step $stepNumber"
            }
        }
    }
}