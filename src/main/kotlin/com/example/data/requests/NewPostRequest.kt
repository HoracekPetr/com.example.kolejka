package com.example.data.requests

data class NewPostRequest(
    val title: String,
    val description: String,
    val limit: Int,
    val type: Int,
    val date: String,
    val location: String,
    val price: Int?,
    val postImageURL: String
)
