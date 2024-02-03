package com.oudombun.foody.bindingAdapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import coil.load
import com.example.foody.R

class RecipeBinding {
    companion object{

        @BindingAdapter("setNumberOfLikes")
        @JvmStatic
        fun setNumberOfLikes(textView: TextView,likes:Int){
            textView.text = likes.toString()
        }

        @BindingAdapter("setReadyInMn")
        @JvmStatic
        fun setReadyInMn(textView: TextView,minutes:Int){
            textView.text = minutes.toString()
        }

        @BindingAdapter("applyVeganColor")
        @JvmStatic
        fun applyVeganColor(view: View,isVegan:Boolean){
            if(isVegan){
                when(view){
                    is TextView ->{
                        view.setTextColor(ContextCompat.getColor(view.context,R.color.green))
                    }
                    is ImageView ->{
                        view.setColorFilter(ContextCompat.getColor(view.context,R.color.green))
                    }
                }
            }
        }

        @BindingAdapter("loadImageFromUrl")
        @JvmStatic
        fun loadImageFromUrl(imageView: ImageView,url:String){
            imageView.load(url){
                crossfade(600)
            }
        }
    }
}