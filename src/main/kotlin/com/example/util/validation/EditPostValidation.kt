package com.example.util.validation

sealed class EditPostValidation{
    object EmptyFieldError: EditPostValidation()
    object DescriptionTooLong: EditPostValidation()
    object TitleTooLong: EditPostValidation()
    data class Success(val request: Boolean): EditPostValidation()
}
