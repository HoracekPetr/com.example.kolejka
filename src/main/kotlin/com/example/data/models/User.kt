package com.example.data.models

import com.example.data.responses.ProfileResponse
import com.example.util.Constants
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class  User(
    @BsonId
    val id: String = ObjectId().toString(),
    val email: String,
    val username: String,
    val password: String,
    val profilePictureURL: String,
    val bannerR: Float = 255f,
    val bannerG: Float = 255f,
    val bannerB: Float = 255f
)
