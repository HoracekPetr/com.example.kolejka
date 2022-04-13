package com.example.data.requests.user

data class UpdateUserRequest(
    val username: String,
    val bannerR: Float,
    val bannerG: Float,
    val bannerB: Float,
    val profilePictureURL: String?
)
