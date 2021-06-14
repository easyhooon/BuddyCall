package kr.ac.konkuk.koogle.Model.Entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationLatLngEntity(
    val latitude: Float, //위도
    val longtitude: Float //경도
): Parcelable
