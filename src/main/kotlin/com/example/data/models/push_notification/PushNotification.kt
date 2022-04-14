package com.example.data.models.push_notification

import com.google.gson.annotations.SerializedName

data class PushNotification(
    @SerializedName("included_segments")
    val includedSegments: List<String>,
    val contents: PushNotificationMessage,
    val heading: PushNotificationMessage,
    @SerializedName("app_id")
    val appId: String
)
