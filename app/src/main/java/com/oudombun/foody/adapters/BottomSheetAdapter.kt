package com.oudombun.foody.adapters

//import com.bumptech.glide.Glide
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.oudombun.foody.databinding.ItemInstructionBinding
import com.oudombun.foody.models.StepDisplay
import com.oudombun.foody.util.Constants.Companion.BASE_IMAGE_URL
import javax.inject.Inject


class BottomSheetAdapter : RecyclerView.Adapter<BottomSheetAdapter.MyViewHolder>() {
    var instructions:ArrayList<StepDisplay>? =null
    class MyViewHolder(private val binding: ItemInstructionBinding,private val context: Context,private val glide:RequestManager) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(result: StepDisplay){
            binding.stepNumber.text = "Step ${result.number}"
            binding.instructionText.text = result.step
            // Image Handling Logic:
            binding.imageContainer.removeAllViews() // Clear any existing views

            if (result.ingredientsImageList.isNotEmpty()) { // Check if there are images
                for (imageUrl in result.ingredientsImageList) {
                    val imageView = ImageView(context)
                    glide.load("$BASE_IMAGE_URL/$imageUrl")
                        .override(400, 400) // Maintain aspect ratio
                        .fitCenter()
                        .into(imageView);

                    binding.imageContainer.addView(imageView)
                }
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, context: Context, glide: RequestManager): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemInstructionBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding,context, glide )
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val glide = Glide.with(parent.context)
        return MyViewHolder.from(parent,parent.context,glide)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentRecipe = instructions?.get(position)
        if (currentRecipe != null) {
            holder.bind(currentRecipe)
        }
    }

    override fun getItemCount(): Int {
        return instructions?.size ?:0
    }
    fun setData(newInstructions: ArrayList<StepDisplay>?) {
        instructions = newInstructions
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }
}