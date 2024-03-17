package com.oudombun.foody.ui.fragments.ingredients

import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.oudombun.foody.R
import com.oudombun.foody.adapters.AnalyzedInstructionAdapter
import com.oudombun.foody.adapters.IngredientsAdapter
import com.oudombun.foody.models.ResultData
import com.oudombun.foody.models.Step
import com.oudombun.foody.models.StepDisplay
import com.oudombun.foody.util.Constants.Companion.RECIPE_RESULT_KEY
import kotlinx.android.synthetic.main.fragment_ingredients.view.*

class IngredientsFragment : Fragment() {

    private val mAdapter: AnalyzedInstructionAdapter by lazy { AnalyzedInstructionAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ingredients, container, false)

        val args = arguments
        val myBundle: ResultData? = args?.getParcelable(RECIPE_RESULT_KEY)

//        setupRecyclerView(view)
//        myBundle?.extendedIngredients?.let { mAdapter.setData(it) }

        myBundle?.extendedIngredients?.let {
            val recipeSteps = mutableListOf<StepDisplay>()

            for (instruction in it) {
                for (step in instruction.steps) {
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
            }

            val stepsContainer = view.findViewById<LinearLayout>(R.id.recipe_steps_layout)

            var ingredients =""
            var equipment =""

            for (step in recipeSteps) {
                // Create TextViews
                val headerTextView = TextView(requireContext())
                headerTextView.text = "Step ${step.number}"
                headerTextView.textSize = 18f
                headerTextView.setTypeface(null, Typeface.BOLD)

                val descriptionTextView = TextView(requireContext())
                descriptionTextView.text = step.step

                if(step.ingredients.isNotEmpty()){
                    ingredients += step.ingredients.joinToString(", ")
                }
                if(step.equipment.isNotEmpty()){
                    equipment += step.equipment.joinToString(", ")
                }

                // Add views to the layout
                stepsContainer.addView(descriptionTextView)
                stepsContainer.addView(headerTextView)

            }
            val ingredientsTextView = TextView(requireContext())
            ingredientsTextView.text = ingredients
            stepsContainer.addView(ingredientsTextView)


            val equipmentTextView = TextView(requireContext())
            equipmentTextView.text = equipment
            stepsContainer.addView(equipmentTextView)

            // ... (similarly add equipment if needed)

        }

        return view
    }

//    private fun setupRecyclerView(view: View) {
//        view.ingredients_recyclerview.adapter = mAdapter
//        view.ingredients_recyclerview.layoutManager = LinearLayoutManager(requireContext())
//    }

}