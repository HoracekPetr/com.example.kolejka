package com.example.util

sealed class CommentValidationEvent {
    object EmptyFieldError: CommentValidationEvent()
    object CommentTooLong: CommentValidationEvent()
    data class Success(val commentId: String): CommentValidationEvent()
}