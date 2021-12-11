package com.example.util

object ApiResponseMessages {
    const val USER_ALREADY_EXISTS = "User with this email already exists."
    const val USER_NOT_FOUND = "User doesn't exist."
    const val FIELDS_BLANK = "The fields can't be empty."
    const val INVALID_CREDENTIALS = "Incorrect email/password, please try again."
    const val COMMENT_TOO_LONG = "The comment can't exceed ${Constants.MAX_COMMENT_LENGTH} characters."
}