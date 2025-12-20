package com.oudombun.chelfy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oudombun.chelfy.databinding.SkeletonFilterCardBinding

class SkeletonFilterAdapter(private val itemCount: Int = 5) :
    RecyclerView.Adapter<SkeletonFilterAdapter.SkeletonViewHolder>() {

    class SkeletonViewHolder(binding: SkeletonFilterCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkeletonViewHolder {
        val binding = SkeletonFilterCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SkeletonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SkeletonViewHolder, position: Int) {
        // No binding needed for skeleton
    }

    override fun getItemCount(): Int = itemCount
}