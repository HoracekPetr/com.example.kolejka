package com.example.util

sealed class ValidationEvent{
    object EmptyFieldError: ValidationEvent()
    object Success: ValidationEvent()
}
