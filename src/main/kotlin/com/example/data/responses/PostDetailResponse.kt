package com.example.data.responses

import com.example.data.models.Post

data class PostDetailResponse(
    val post: Post?,
    val requesterId: String
)
