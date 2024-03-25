package com.oudombun.chelfy.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


data class ResultData(
    @SerializedName("aggregateLikes")
    val aggregateLikes: Int,
    @SerializedName("cheap")
    val cheap: Boolean,
    @SerializedName("dairyFree")
    val dairyFree: Boolean,
    @SerializedName("glutenFree")
    val glutenFree: Boolean,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("readyInMinutes")
    val readyInMinutes: Int,
    @SerializedName("sourceName")
    val sourceName: String,
    @SerializedName("sourceUrl")
    val sourceUrl: String,
    @SerializedName("summary")
    val summary: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("vegan")
    val vegan: Boolean,
    @SerializedName("vegetarian")
    val vegetarian: Boolean,
    @SerializedName("veryHealthy")
    val veryHealthy: Boolean,
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(aggregateLikes)
        parcel.writeByte(if (cheap) 1 else 0)
        parcel.writeByte(if (dairyFree) 1 else 0)
        parcel.writeByte(if (glutenFree) 1 else 0)
        parcel.writeInt(id)
        parcel.writeString(image)
        parcel.writeInt(readyInMinutes)
        parcel.writeString(sourceName)
        parcel.writeString(sourceUrl)
        parcel.writeString(summary)
        parcel.writeString(title)
        parcel.writeByte(if (vegan) 1 else 0)
        parcel.writeByte(if (vegetarian) 1 else 0)
        parcel.writeByte(if (veryHealthy) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ResultData> {
        override fun createFromParcel(parcel: Parcel): ResultData {
            return ResultData(parcel)
        }

        override fun newArray(size: Int): Array<ResultData?> {
            return arrayOfNulls(size)
        }
    }
}
