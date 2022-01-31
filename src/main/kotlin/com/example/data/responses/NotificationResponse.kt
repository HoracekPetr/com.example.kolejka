package com.example.data.responses

data class NotificationResponse(
    val timestamp: Long,
    val parentId: String,
    val type: Int,
    val username: String,
    val id: String
)
