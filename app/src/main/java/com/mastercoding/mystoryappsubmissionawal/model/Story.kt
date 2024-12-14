package com.mastercoding.mystoryappsubmissionawal.model

import com.google.gson.annotations.SerializedName

data class Story(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("photoUrl") val photoUrl: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?,
    @SerializedName("lat") val lat: Float?,
    @SerializedName("lon") val lon: Float?
)