package com.oudombun.chelfy.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class Ingredient(
    val id: Int,
    val name: String,
    val localizedName: String,
    val image: String
) : Parcelable {

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(name)
        dest.writeString(localizedName)
        dest.writeString(image)
    }

    companion object CREATOR : Parcelable.Creator<Ingredient> {
        override fun createFromParcel(parcel: Parcel): Ingredient {
            return Ingredient(
                parcel.readInt(),
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!
            )
        }

        override fun newArray(size: Int): Array<Ingredient?> {
            return arrayOfNulls(size)
        }
    }
}


data class Equipment(
    val id: Int,
    val name: String,
    val localizedName: String,
    val image: String
) : Parcelable {

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(name)
        dest.writeString(localizedName)
        dest.writeString(image)
    }

    companion object CREATOR : Parcelable.Creator<Equipment> {
        override fun createFromParcel(parcel: Parcel): Equipment {
            return Equipment(
                parcel.readInt(),
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!
            )
        }

        override fun newArray(size: Int): Array<Equipment?> {
            return arrayOfNulls(size)
        }
    }
}
data class Length(
    val number: Int,
    val unit: String
) : Parcelable {

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(number)
        dest.writeString(unit)
    }

    companion object CREATOR : Parcelable.Creator<Length> {
        override fun createFromParcel(parcel: Parcel): Length {
            return Length(
                parcel.readInt(),
                parcel.readString()!!
            )
        }

        override fun newArray(size: Int): Array<Length?> {
            return arrayOfNulls(size)
        }
    }
}

data class Step(
    val number: Int,
    val step: String,
    val ingredients: List<Ingredient>,
    val equipment: List<Equipment>,
    val length: Length?
) : Parcelable {

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(number)
        dest.writeString(step)
        dest.writeTypedList(ingredients)
        dest.writeTypedList(equipment)
        dest.writeParcelable(length, flags)
    }

    companion object CREATOR : Parcelable.Creator<Step> {
        override fun createFromParcel(parcel: Parcel): Step {
            return Step(
                parcel.readInt(),
                parcel.readString()!!,
                parcel.createTypedArrayList(Ingredient.CREATOR)!!,
                parcel.createTypedArrayList(Equipment.CREATOR)!!,
                parcel.readParcelable(Length::class.java.classLoader)
            )
        }

        override fun newArray(size: Int): Array<Step?> {
            return arrayOfNulls(size)
        }
    }
}


data class AnalyzedInstruction(
    val name: String,
    val steps: List<Step>
) : Parcelable {

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeTypedList(steps)
    }

    companion object CREATOR : Parcelable.Creator<AnalyzedInstruction> {
        override fun createFromParcel(parcel: Parcel): AnalyzedInstruction {
            return AnalyzedInstruction(
                parcel.readString()!!,
                parcel.createTypedArrayList(Step.CREATOR)!!
            )
        }

        override fun newArray(size: Int): Array<AnalyzedInstruction?> {
            return arrayOfNulls(size)
        }
    }
}
data class StepDisplay(
    val number: Int,
    val step: String,
    val ingredients: List<String>,
    val ingredientsImageList: List<String>,
    val equipment: List<String>,
    val length: Length?
): Parcelable {

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(number)
        dest.writeString(step)
        dest.writeStringList(ingredients)
        dest.writeStringList(ingredientsImageList)
        dest.writeStringList(equipment)
        dest.writeParcelable(length, flags)
    }

    companion object CREATOR : Parcelable.Creator<StepDisplay> {
        override fun createFromParcel(parcel: Parcel): StepDisplay {
            return StepDisplay(
                parcel.readInt(),
                parcel.readString()!!,
                parcel.createStringArrayList()!!,
                parcel.createStringArrayList()!!,
                parcel.createStringArrayList()!!,
                parcel.readParcelable(Length::class.java.classLoader)
            )
        }

        override fun newArray(size: Int): Array<StepDisplay?> {
            return arrayOfNulls(size)
        }
    }
}
