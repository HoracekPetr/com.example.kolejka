package com.example.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Post(
    @BsonId
    val id: String = ObjectId().toString(),
    val userId: String,
    val title: String,
    val username: String,
    val description: String,
    val postPictureUrl: String,
    val members: List<String>,
    val available: Int,
    val limit: Int?,
    val type: Int
)
