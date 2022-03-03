package com.example.data.requests

data class UpdateUserRequest(
    val username: String,
    val bannerR: Float,
    val bannerG: Float,
    val bannerB: Float,
    val profilePictureURL: String?
)
