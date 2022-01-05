package com.example.service

import com.example.data.models.User
import com.example.data.repositories.user.UserRepository
import com.example.data.requests.RegisterAccountRequest
import com.example.data.requests.LoginAccountRequest
import com.example.data.requests.UpdateProfileRequest
import com.example.data.responses.ProfileResponse
import com.example.util.validation.ValidationEvent

class UserService(
    private val userRepository: UserRepository
) {

    suspend fun doesUserWithEmailExist(email: String): Boolean {
        return userRepository.getUserByEmail(email) != null
    }

/*    suspend fun doesEmailBelongToUserId(email: String, userId: String): Boolean {
        return userRepository.doesEmailBelongToUserId(email, userId)
    }*/

    suspend fun isPasswordCorrect(email: String, password: String): Boolean {
        return userRepository.doesPasswordForUserMatch(email, password)
    }

    suspend fun getUserByEmail(email: String): User? {
        return userRepository.getUserByEmail(email)
    }

    suspend fun getUsernameById(id: String): String? {
        return userRepository.getUsernameById(id)
    }

    suspend fun getUserProfile(userId: String): ProfileResponse? {

        val user = userRepository.getUserById(userId) ?: return null

        return ProfileResponse(
            username = user.username,
            profilePictureUrl = user.profilePictureURL,
            bannerR = user.bannerR,
            bannerG = user.bannerG,
            bannerB = user.bannerB
        )
    }

/*    fun isValidPassword(enteredPassword: String, actualPassword: String): Boolean {
        return enteredPassword == actualPassword
    }*/

    suspend fun createUser(request: RegisterAccountRequest) {
        userRepository.createUser(
            User(
                email = request.email,
                username = request.username,
                password = request.password,
                profilePictureURL = ""
            )
        )
    }

    suspend fun updateUser(
        userId: String,
        profilePictureUrl: String,
        updateProfileRequest: UpdateProfileRequest
    ): Boolean = userRepository.updateUser(userId, profilePictureUrl, updateProfileRequest)

    fun validateCreateAccountRequest(request: RegisterAccountRequest): ValidationEvent {
        return if (request.email.isBlank() || request.password.isBlank() || request.username.isBlank()) {
            ValidationEvent.EmptyFieldError
        } else ValidationEvent.Success
    }

    fun validateLoginRequest(request: LoginAccountRequest): ValidationEvent {
        return if (request.email.isBlank() || request.password.isBlank()) {
            ValidationEvent.EmptyFieldError
        } else ValidationEvent.Success
    }
}