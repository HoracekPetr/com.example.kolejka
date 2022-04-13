package com.example.data.repositories.user

import com.example.data.models.User
import com.example.data.requests.user.UpdateUserRequest
import com.example.security.checkHashForPassword
import com.example.security.getHashWithSalt
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

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
                id = user.id,
                accessRights = user.accessRights
            )
        ).wasAcknowledged()
    }

    override suspend fun doesPasswordForUserMatch(email: String, password: String): Boolean {
        val user = getUserByEmail(email) ?: return false
        return checkHashForPassword(password, user.password)
    }

    override suspend fun doesEmailBelongToUserId(email: String, userId: String): Boolean {
        return users.findOneById(userId)?.email == email
    }

    override suspend fun changeUserPassword(userId: String?, newPassword: String): Boolean {
        return users.updateOne(
            filter = User::id eq userId,
            update = setValue(User::password, getHashWithSalt(newPassword))
        ).wasAcknowledged()
    }
}