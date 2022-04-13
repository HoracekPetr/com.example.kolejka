package com.example.data.requests.auth

data class RegisterAccountRequest(
    val email: String,
    val username: String,
    val password: String
)
