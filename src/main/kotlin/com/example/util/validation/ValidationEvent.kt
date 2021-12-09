package com.example.util.validation

sealed class ValidationEvent{
    object EmptyFieldError: ValidationEvent()
    object Success: ValidationEvent()
}
