package com.example.data.repositories.user

import com.example.data.models.User
import com.example.data.requests.UpdateProfileRequest

interface UserRepository {

    suspend fun createUser(user: User)

    suspend fun getUserById(id: String): User?

    suspend fun getUsernameById(id: String): String?

    suspend fun getUserByEmail(email: String): User?

    suspend fun getUserProfileUrl(userId: String): String?

    suspend fun updateUser(userId: String, profilePictureUrl: String?, updateProfileRequest: UpdateProfileRequest): Boolean

    suspend fun doesPasswordForUserMatch(email: String, password: String): Boolean

    suspend fun doesEmailBelongToUserId(email: String, userId: String): Boolean
}