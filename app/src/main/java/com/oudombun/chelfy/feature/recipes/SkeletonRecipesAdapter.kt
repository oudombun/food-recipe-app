package com.oudombun.chelfy.feature.recipes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oudombun.chelfy.R

class SkeletonRecipesAdapter(private val itemCount: Int = 5) :
    RecyclerView.Adapter<SkeletonRecipesAdapter.SkeletonViewHolder>() {

    class SkeletonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkeletonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.skeleton_recipes_row, parent, false)
        return SkeletonViewHolder(view)
    }

    override fun onBindViewHolder(holder: SkeletonViewHolder, position: Int) {
        // No binding needed for skeleton
    }

    override fun getItemCount(): Int = itemCount
}

