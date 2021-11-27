package com.example.data.requests

data class CreatePostRequest(
    val userId: String,
    val title: String,
    val description: String,
    val postImageUrl: String,
    val limit: Int,
    val isEvent: Boolean,
    val isOffer: Boolean
)