package com.rodrigmatrix.sippa.entity


import com.google.gson.annotations.SerializedName

data class Version(
    @SerializedName("updateType")
    val updateType: Int,
    @SerializedName("versionCode")
    val versionCode: Int
)