package com.example.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Comment(
    @BsonId
    val id: String = ObjectId().toString(),
    val userId: String = "",
    val postId: String,
    val username: String = "",
    val profilePictureUrl: String = "",
    val comment: String,
    val timestamp: Long
)
