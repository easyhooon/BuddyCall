package kr.ac.konkuk.koogle.Model.Entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationLatLngEntity(
    val latitude: Float, //위도
    val longitude: Float //경도
): Parcelable
{
    constructor(): this(0.0f, 0.0f)
}
