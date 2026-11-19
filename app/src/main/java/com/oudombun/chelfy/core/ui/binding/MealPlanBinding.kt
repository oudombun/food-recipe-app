package com.oudombun.chelfy.core.ui.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.oudombun.chelfy.R

object MealPlanBinding {
    @JvmStatic
    @BindingAdapter("setCalories")
    fun setCalories(view: TextView, calories: Double) {
        view.text = view.context.getString(R.string.meal_plan_calories, calories)
    }

    @JvmStatic
    @BindingAdapter("setProtein")
    fun setProtein(view: TextView, protein: Double) {
        view.text = view.context.getString(R.string.meal_plan_protein, protein)
    }
}
