package com.example.data.repositories.user

import com.example.data.models.User

interface UserRepository {

    suspend fun createUser(user: User)

    suspend fun getUserById(id: String): User?

    suspend fun getUserByEmail(email: String): User?

    suspend fun doesPasswordForUserMatch(email: String, password: String): Boolean

    suspend fun doesEmailBelongToUserId(email: String, userId: String): Boolean
}