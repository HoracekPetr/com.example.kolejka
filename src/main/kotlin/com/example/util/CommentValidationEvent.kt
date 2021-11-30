package com.example.util

sealed class CommentValidationEvent {
    object EmptyFieldError: CommentValidationEvent()
    object CommentTooLong: CommentValidationEvent()
    object Success: CommentValidationEvent()
}