package com.example.feproblem1.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Planet(
    val name: String,
    val distance: Int
) : Parcelable

@Parcelize
data class Vehicle(
    val name: String,
    @SerializedName("total_no") var totalNo: Int,
    @SerializedName("max_distance") val maxDist: Int,
    val speed: Int
) : Parcelable

// ✅ No need to parcelize a simple token response
data class TokenResponse(
    val token: String
)

// ✅ Corrected data type for planetNames
data class FindFalconeRequest(
    val token: String,
    @SerializedName("planet_names") val planetNames: String,  // FIXED: Changed from String to List<String>
    @SerializedName("vehicle_names") val vehicleNames: List<String>
)

data class FindFalconeResponse(
    @SerializedName("planet_name") val planetName: String?,
    val status: String
)
