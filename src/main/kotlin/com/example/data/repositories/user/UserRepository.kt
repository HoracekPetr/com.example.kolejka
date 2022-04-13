package com.example.data.repositories.user

import com.example.data.models.User
import com.example.data.requests.user.UpdateUserRequest

interface UserRepository {

    suspend fun createUser(user: User)

    suspend fun getUserById(id: String): User?

    suspend fun getUsernameById(id: String): String?

    suspend fun getUserByEmail(email: String): User?

    suspend fun getUserProfileUrl(userId: String): String?

    suspend fun updateUserInfo(userId: String, updateProfileRequest: UpdateUserRequest): Boolean

    suspend fun changeUserPassword(userId: String?, newPassword: String): Boolean

    suspend fun doesPasswordForUserMatch(email: String, password: String): Boolean

    suspend fun doesEmailBelongToUserId(email: String, userId: String): Boolean
}