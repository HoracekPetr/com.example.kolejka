package com.example.data.responses

data class ProfileResponse(
    val userId: String,
    val username: String,
    val profilePictureUrl: String,
    val bannerR: Float,
    val bannerG: Float,
    val bannerB: Float
)
