package com.example.service

import com.example.data.models.push_notification.PushNotification
import com.example.data.repositories.push_notifications.PushNotificationRepository
import io.ktor.client.*

class PushNotificationService(
    private val pushNotificationRepository: PushNotificationRepository
) {
    suspend fun sendPushNotification(pushNotification: PushNotification,client: HttpClient, apiKey: String)
    = pushNotificationRepository.sendPushNotification(pushNotification, client, apiKey)
}