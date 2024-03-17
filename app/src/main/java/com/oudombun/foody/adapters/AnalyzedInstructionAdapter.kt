package com.oudombun.foody.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.oudombun.foody.R
import com.oudombun.foody.models.AnalyzedInstruction
import com.oudombun.foody.models.StepDisplay
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
        private val ingredientTextview: TextView = itemView.findViewById(R.id.ingredient_consistency)
        private val ingredientOriginTextview: TextView = itemView.findViewById(R.id.ingredient_original)

        @SuppressLint("SetTextI18n")
        fun bind(analyzedInstruction: AnalyzedInstruction) {
            val stepNumber = analyzedInstruction.steps[0].number
            stepNumberTextView.text = "Step $stepNumber"
            val recipeSteps = mutableListOf<StepDisplay>()
                for (step in analyzedInstruction.steps) {
                    val ingredientsList = step.ingredients.map { it.name } // Extract ingredient names
                    val equipmentList = step.equipment.map { it.name } // Extract equipment names

                    val formattedStep = StepDisplay(
                        step.number,
                        step.step,
                        ingredientsList,
                        equipmentList,
                        step.length
                    )

                    recipeSteps.add(formattedStep)
                }
            ingredientTextview.text= recipeSteps.toString()
            // Display the step number (assuming there's only one step for simplicity)
//            if (analyzedInstruction.steps.isNotEmpty()) {
//                val stepNumber = analyzedInstruction.steps[0].number
//                stepNumberTextView.text = "Step $stepNumber"
//            }
        }
    }
}