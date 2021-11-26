package com.example.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Notification(
    @BsonId
    val id: String = ObjectId().toString(),
    val byUserID: String,
    val toPostID: String,
    val timestamp: Long,
    val type: Int
)
