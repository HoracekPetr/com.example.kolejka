package com.example.data.requests.post

data class UpdatePostRequest(
    val postId: String,
    val title: String,
    val description: String,
    val limit: Int?,
    val date: String,
    val location: String,
    val price: Int?,
    val postPictureUrl: String?
)