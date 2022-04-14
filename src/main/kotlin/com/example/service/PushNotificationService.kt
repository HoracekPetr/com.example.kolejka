package com.example.service

import com.example.data.models.push_notification.PushNotification
import com.example.data.repositories.push_notifications.PushNotificationRepository

class PushNotificationService(
    private val pushNotificationRepository: PushNotificationRepository
) {
    suspend fun sendPushNotification(pushNotification: PushNotification, apiKey: String) = pushNotificationRepository.sendPushNotification(pushNotification, apiKey)
}