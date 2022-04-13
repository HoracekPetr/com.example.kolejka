package com.example.util.validation

sealed class CreateNewsValidation{
    object EmptyFieldError: CreateNewsValidation()
    object TitleTooLong: CreateNewsValidation()
    data class Success(val request: Boolean): CreateNewsValidation()
}
