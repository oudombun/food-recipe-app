package com.oudombun.chelfy.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class ExtendedIngredient(
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("consistency")
    val consistency: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("original")
    val original: String,
    @SerializedName("unit")
    val unit: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(amount)
        parcel.writeString(consistency)
        parcel.writeString(image)
        parcel.writeString(name)
        parcel.writeString(original)
        parcel.writeString(unit)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ExtendedIngredient> {
        override fun createFromParcel(parcel: Parcel): ExtendedIngredient {
            return ExtendedIngredient(parcel)
        }

        override fun newArray(size: Int): Array<ExtendedIngredient?> {
            return arrayOfNulls(size)
        }
    }
}