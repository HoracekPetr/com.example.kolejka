package com.example.data.requests.post

data class CreatePostRequest(
    val title: String,
    val description: String,
    val limit: Int,
    val type: Int,
    val date: String,
    val location: String
)