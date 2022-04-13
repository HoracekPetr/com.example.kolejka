package com.example.data.requests.comment

data class CreateCommentRequest(
    val comment: String,
    val postId: String,
)