package com.example.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class  User(
    @BsonId
    val id: String = ObjectId().toString(),
    val email: String,
    val username: String,
    val password: String,
    val profileImageURL: String,
    val bannerR: Float = 255f,
    val bannerG: Float = 255f,
    val bannerB: Float = 255f
)
