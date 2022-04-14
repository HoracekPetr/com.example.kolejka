package com.example.data.repositories.push_notifications

import com.example.data.models.push_notification.PushNotification

interface PushNotificationRepository {

    suspend fun sendPushNotification(pushNotification: PushNotification, apiKey: String): Boolean
}