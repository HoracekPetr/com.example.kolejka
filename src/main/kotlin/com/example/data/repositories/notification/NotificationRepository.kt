package com.example.data.repositories.notification

import com.example.data.models.Notification
import com.example.data.models.NotificationCount
import com.example.data.responses.NotificationResponse
import com.example.util.Constants

interface NotificationRepository {

    suspend fun getNotificationsForUser(
        userId: String, page: Int = 0, pageSize: Int = Constants.NOTIFICATIONS_PAGE_SIZE
    ): List<NotificationResponse>

    suspend fun createNotification(notification: Notification): Boolean

    suspend fun deleteNotification(notificationId: String): Boolean

    suspend fun updateNotificationCount(userId: String, notificationCount: NotificationCount): Boolean

    suspend fun getNotificationCount(userId: String): Int
}