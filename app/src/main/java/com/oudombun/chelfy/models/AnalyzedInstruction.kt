package com.oudombun.chelfy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Ingredient(
    val id: Int,
    val name: String,
    val localizedName: String,
    val image: String
) : Parcelable

@Parcelize
data class Equipment(
    val id: Int,
    val name: String,
    val localizedName: String,
    val image: String
) : Parcelable

@Parcelize
data class Length(
    val number: Int,
    val unit: String
) : Parcelable

@Parcelize
data class Step(
    val number: Int,
    val step: String,
    val ingredients: List<Ingredient>,
    val equipment: List<Equipment>,
    val length: Length?
) : Parcelable

@Parcelize
data class AnalyzedInstruction(
    val name: String,
    val steps: List<Step>
) : Parcelable

@Parcelize
data class StepDisplay(
    val number: Int,
    val step: String,
    val ingredients: List<String>,
    val ingredientsImageList: List<String>,
    val equipment: List<String>,
    val length: Length?
):Parcelable