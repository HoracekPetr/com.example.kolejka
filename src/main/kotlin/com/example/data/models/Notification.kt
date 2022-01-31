package com.example.data.models

import com.example.data.util.NotificationAction
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Notification(
    @BsonId
    val id: String = ObjectId().toString(),
    val username: String,
    val byUserID: String,
    val toUserID: String,
    val parentID: String,
    val timestamp: Long,
    val type: Int
)
