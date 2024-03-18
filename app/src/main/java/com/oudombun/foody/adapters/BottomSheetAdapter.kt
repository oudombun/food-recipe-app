package com.oudombun.foody.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oudombun.foody.databinding.InstructionBottomSheetBinding
import com.oudombun.foody.models.StepDisplay

class BottomSheetAdapter : RecyclerView.Adapter<BottomSheetAdapter.MyViewHolder>() {

    private var instructions = emptyArray<StepDisplay>()

    class MyViewHolder(private val binding: InstructionBottomSheetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(result: StepDisplay){
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = InstructionBottomSheetBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentRecipe = instructions[position]
        holder.bind(currentRecipe)
    }

    override fun getItemCount(): Int {
        return instructions.size
    }
    fun setData(newInstructions: Array<StepDisplay>) {
        instructions = newInstructions
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }
}