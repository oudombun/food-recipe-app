package com.oudombun.chelfy.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.oudombun.chelfy.core.model.ResultData
import com.oudombun.chelfy.core.common.Constants.Companion.FAVORITE_RECIPES_TABLE

@Entity(tableName = FAVORITE_RECIPES_TABLE)
class FavoritesEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var result: ResultData
)