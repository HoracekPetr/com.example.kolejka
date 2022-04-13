package com.example.data.requests.auth

data class ChangePasswordRequest(
    val userId: String?,
    val newPassword: String
)
