package com.example.data.requests.user

data class UpdateProfileRequest(
    val username: String,
    val bannerR: Float,
    val bannerG: Float,
    val bannerB: Float
)