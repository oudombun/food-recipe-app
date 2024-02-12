package com.oudombun.foody.util

import androidx.recyclerview.widget.DiffUtil
import com.oudombun.foody.models.ResultData

class RecipeDiffUtil(
    private val oldList: List<ResultData>,
    private val newList: List<ResultData>
) :DiffUtil.Callback(){
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}