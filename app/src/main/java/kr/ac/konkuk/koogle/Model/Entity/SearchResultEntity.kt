package kr.ac.konkuk.koogle.Model.Entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchResultEntity (
    val fullAddress: String,
    val name: String,
    val locationLatLng: LocationLatLngEntity?
): Parcelable
{
    constructor(): this("","",null)
}
