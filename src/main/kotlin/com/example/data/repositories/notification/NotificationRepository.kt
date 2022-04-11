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

    suspend fun deleteNotificationsForPost(postId: String): Boolean

    suspend fun deleteAllNotificationsForUser(userId: String): Boolean

    suspend fun updateNotificationCount(userId: String, notificationCount: NotificationCount): Boolean

    suspend fun setNotificationsToZero(userId: String): Boolean

    suspend fun getNotificationCount(userId: String): Int

    suspend fun updateNotificationInfo(userId: String): Boolean
}