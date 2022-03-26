package com.example.data.requests

data class ChangePasswordRequest(
    val userId: String?,
    val newPassword: String
)
