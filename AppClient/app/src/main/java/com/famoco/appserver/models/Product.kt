package com.famoco.appserver.models

import android.os.Parcel
import android.os.Parcelable

data class Product(
    var name: String,
    var quantity: Int,
    var cost: Float) : Parcelable {

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Product> {
            override fun createFromParcel(parcel: Parcel) = Product(parcel)
            override fun newArray(size: Int) = arrayOfNulls<Product>(size)
        }
    }

    private constructor(parcel: Parcel) : this(
        name = parcel.readString()!!,
        quantity = parcel.readInt(),
        cost = parcel.readFloat(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(quantity)
        parcel.writeFloat(cost)
    }

    override fun describeContents() = 0

    fun readFromParcel(parcel : Parcel) {
        this.name = parcel.readString()!!
        this.quantity = parcel.readInt()
        this.cost = parcel.readFloat()
    }
}