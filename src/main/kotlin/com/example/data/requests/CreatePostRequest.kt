package com.example.data.requests

data class CreatePostRequest(
    val title: String,
    val description: String,
    val limit: Int,
    val type: Int
)