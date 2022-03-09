package com.example.data.repositories.user

import com.example.data.models.User
import com.example.data.requests.UpdateProfileRequest
import com.example.data.requests.UpdateUserRequest
import com.example.security.checkHashForPassword
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class UserRepositoryImpl(
    db: CoroutineDatabase
) : UserRepository {

    private val users = db.getCollection<User>()

    override suspend fun createUser(user: User) {
        users.insertOne(user)
    }

    override suspend fun getUserById(id: String): User? {
        return users.findOneById(id)
    }

    override suspend fun getUsernameById(id: String): String? {
        return users.findOneById(id)?.username
    }

    override suspend fun getUserByEmail(email: String): User? {
        return users.findOne(User::email eq email)
    }

    override suspend fun getUserProfileUrl(userId: String): String? {
        return users.findOneById(userId)?.profilePictureURL
    }

    override suspend fun updateUserInfo(userId: String, updateProfileRequest: UpdateUserRequest): Boolean {
        val user = getUserById(userId) ?: return false
        return users.updateOneById(
            id = user.id,
            update = User(
                email = user.email,
                username = updateProfileRequest.username,
                password = user.password,
                profilePictureURL = updateProfileRequest.profilePictureURL ?: user.profilePictureURL,
                bannerR = updateProfileRequest.bannerR,
                bannerG = updateProfileRequest.bannerG,
                bannerB = updateProfileRequest.bannerB,
                id = user.id
            )
        ).wasAcknowledged()
    }

    override suspend fun doesPasswordForUserMatch(email: String, password: String): Boolean {
        val user = getUserByEmail(email) ?: return false
        //return user?.password == password
        return checkHashForPassword(password, user.password)
    }

    override suspend fun doesEmailBelongToUserId(email: String, userId: String): Boolean {
        return users.findOneById(userId)?.email == email
    }
}