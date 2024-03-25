package com.oudombun.chelfy.bindingAdapter

import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import com.google.android.material.card.MaterialCardView
import com.oudombun.chelfy.data.database.entities.MealPlanEntity
import com.oudombun.chelfy.models.FoodJoke
import com.oudombun.chelfy.models.MealPlanResponse
import com.oudombun.chelfy.models.ResultData
import com.oudombun.chelfy.ui.fragments.mealplan.MealPlanFragmentDirections
import com.oudombun.chelfy.ui.fragments.recipes.RecipesFragmentDirections
import com.oudombun.chelfy.util.NetworkResult
import java.lang.Exception

class MealPlanBinding {

    companion object {

        @BindingAdapter("setCalories")
        @JvmStatic
        fun setCalories(view: TextView,cal:Double){
            view.text = "Total Calories: $cal"
        }
        @BindingAdapter("setProtein")
        @JvmStatic
        fun setProtein(view: TextView,cal:Double){
            view.text = "Total Protein: $cal g"
        }

        @BindingAdapter("readApiResponse3", "readDatabase3", requireAll = false)
        @JvmStatic
        fun setCardAndProgressVisibility(
            view: View,
            apiResponse: NetworkResult<MealPlanResponse>?,
            database: List<MealPlanEntity>?
        ) {
            when (apiResponse) {
                is NetworkResult.Loading -> {
                    when (view) {
                        is ProgressBar -> {
                            view.visibility = View.VISIBLE
                        }
                        is LinearLayout -> {
                            view.visibility = View.INVISIBLE
                        }
                    }
                }
                is NetworkResult.Error -> {
                    when (view) {
                        is ProgressBar -> {
                            view.visibility = View.INVISIBLE
                        }
                        is LinearLayout -> {
                            view.visibility = View.VISIBLE
                            if (database != null) {
                                if (database.isEmpty()) {
                                    view.visibility = View.INVISIBLE
                                }
                            }
                        }
                    }
                }
                is NetworkResult.Success -> {
                    when(view){
                        is ProgressBar -> {
                            view.visibility = View.INVISIBLE
                        }
                        is LinearLayout -> {
                            view.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        @BindingAdapter("readApiResponse4", "readDatabase4", requireAll = true)
        @JvmStatic
        fun setErrorViewsVisibility(
            view: View,
            apiResponse: NetworkResult<MealPlanResponse>?,
            database: List<MealPlanEntity>?
        ){
            if(database != null){
                if(database.isEmpty()){
                    view.visibility = View.VISIBLE
                    if(view is TextView){
                        if(apiResponse != null){
                            view.text = apiResponse.message.toString()
                        }
                    }
                }
            }
            if(apiResponse is NetworkResult.Success){
                view.visibility = View.INVISIBLE
            }
        }

//        @BindingAdapter("onDetailClickListener")
//        @JvmStatic
//        fun onDetailClickListener(recipeRowLayout: MaterialCardView, url: String) {
//            Log.d("TAG", "onRecipeClickListener: ")
//            recipeRowLayout.setOnClickListener {
//                try {
////                    val action =
////                        MealPlanFragmentDirections.actionFoodJokeFragmentToMealPlanDetailFragment(url)
////                    recipeRowLayout.findNavController().navigate(action)
//                } catch (e: Exception) {
//                    Log.d("onRecipeClickListener exception", e.toString())
//                }
//            }
//        }

    }

}