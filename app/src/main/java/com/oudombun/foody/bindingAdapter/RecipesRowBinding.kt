package com.oudombun.foody.bindingAdapter

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import coil.load
import com.oudombun.foody.R
import com.oudombun.foody.models.ResultData
import com.oudombun.foody.ui.fragments.recipes.RecipesFragmentDirections
import de.hdodenhof.circleimageview.CircleImageView
import org.jsoup.Jsoup
import java.lang.Exception

class RecipesRowBinding {

    companion object {
        @BindingAdapter("onRecipeClickListener")
        @JvmStatic
        fun onRecipeClickListener(recipeRowLayout: ConstraintLayout, result: ResultData) {
            Log.d("TAG", "onRecipeClickListener: ")
            recipeRowLayout.setOnClickListener {
                try {
                    val action =
                        RecipesFragmentDirections.actionRecipesFragmentToDetailsActivity(result)
                    recipeRowLayout.findNavController().navigate(action)
                } catch (e: Exception) {
                    Log.d("onRecipeClickListener exception", e.toString())
                }
            }
        }

        @BindingAdapter("loadImageFromUrl")
        @JvmStatic
        fun loadImageFromUrl(imageView: ImageView, imageUrl: String) {
            imageView.load(imageUrl) {
                crossfade(600)
                error(R.drawable.ic_error_placeholder)
            }
        }
        @BindingAdapter("loadImageCircleFromUrl")
        @JvmStatic
        fun loadImageCircleFromUrl(imageView: CircleImageView, imageUrl: String) {
            imageView.load(imageUrl) {
                crossfade(600)
                error(R.drawable.ic_error_placeholder)
            }
        }

        @BindingAdapter("setNumberOfLikes")
        @JvmStatic
        fun setNumberOfLikes(textView: TextView, likes: Int){
            textView.text = likes.toString()
        }

        @BindingAdapter("setNumberOfMinutes")
        @JvmStatic
        fun setNumberOfMinutes(textView: TextView, minutes: Int){
            textView.text = minutes.toString()
        }

        @BindingAdapter("setTextOfMinutes")
        @JvmStatic
        fun setTextOfMinutes(textView: TextView, minutes: Int){
            textView.text = "$minutes mn"
        }

        @BindingAdapter("applyVeganColor")
        @JvmStatic
        fun applyVeganColor(view: View, vegan: Boolean) {
            if(vegan){
                when(view){
                    is TextView -> {
                        view.setTextColor(
                            ContextCompat.getColor(
                                view.context,
                                R.color.green
                            )
                        )
                    }
                    is ImageView -> {
                        view.setColorFilter(
                            ContextCompat.getColor(
                                view.context,
                                R.color.green
                            )
                        )
                    }
                }
            }
        }

        @BindingAdapter("parseHtml")
        @JvmStatic
        fun parseHtml(textView: TextView, description: String?){
            if(description != null) {
                val desc = Jsoup.parse(description).text()
                textView.text = desc
            }
        }
        @BindingAdapter("parseCalories")
        @JvmStatic
        fun parseCalories(textView: TextView, summary: String?){
            if(summary != null) {
                val regexPattern = """<b>(\d+) calories</b>""".toRegex()
                val matchResult = regexPattern.find(summary)
                var calories = matchResult?.groupValues?.get(1)
                if(calories.isNullOrEmpty()) calories= "N/A"
                textView.text = "$calories calories"
            }
        }


    }

}