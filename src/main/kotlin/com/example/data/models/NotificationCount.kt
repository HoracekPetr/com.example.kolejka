package com.example.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class NotificationCount(
    @BsonId
    val id: String = ObjectId().toString(),
    val userId: String,
    val count: Int
)
