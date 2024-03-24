package com.oudombun.chelfy.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.oudombun.chelfy.R

class RecipeStepAdapter(private val context: Context, private val ingredientsList: List<String>) : BaseAdapter() {

    override fun getCount(): Int {
        return ingredientsList.size
    }

    override fun getItem(position: Int): String {
        return ingredientsList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = convertView ?: inflater.inflate(R.layout.recipe_step_item, parent, false)

        val stepImage = view.findViewById<ImageView>(R.id.step_image)
        val stepText = view.findViewById<TextView>(R.id.step_text)

        val ingredient = getItem(position)
        stepText.text = ingredient

        // You can customize how to set image resources here, if needed

        return view
    }
}
