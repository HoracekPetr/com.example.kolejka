package com.example.data.requests

data class CreatePostRequest(
    val title: String,
    val description: String,
    val postImageUrl: String,
    val limit: Int,
    val members: List<String>,
    val isEvent: Boolean,
    val isOffer: Boolean
)