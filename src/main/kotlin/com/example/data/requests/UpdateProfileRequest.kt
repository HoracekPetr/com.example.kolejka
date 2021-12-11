package com.example.data.requests

data class UpdateProfileRequest(
    val username: String,
    val bannerR: Float,
    val bannerG: Float,
    val bannerB: Float,
    val profilePictureChanged: Boolean = false
)