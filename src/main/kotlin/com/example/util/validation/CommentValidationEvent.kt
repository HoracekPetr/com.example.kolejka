package com.example.util.validation

sealed class CommentValidationEvent {
    object EmptyFieldError: CommentValidationEvent()
    object CommentTooLong: CommentValidationEvent()
    object Success: CommentValidationEvent()
}