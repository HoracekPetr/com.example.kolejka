package com.example.data.repositories.push_notifications

import com.example.data.models.push_notification.PushNotification
import io.ktor.client.*

interface PushNotificationRepository {

    suspend fun sendPushNotification(pushNotification: PushNotification, client: HttpClient, apiKey: String): Boolean
}