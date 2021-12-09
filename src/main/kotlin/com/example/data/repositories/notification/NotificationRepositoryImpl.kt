package com.example.data.repositories.notification

import com.example.data.models.Notification
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.insertOne
import org.litote.kmongo.eq

class NotificationRepositoryImpl(
    db: CoroutineDatabase
) : NotificationRepository {

    private val notifications = db.getCollection<Notification>()

    override suspend fun getNotificationsForUser(userId: String, page: Int, pageSize: Int): List<Notification> {

        return notifications.find(Notification::toUserID eq userId)
            .skip(page * pageSize)
            .limit(pageSize)
            .descendingSort(Notification::timestamp)
            .toList()
    }

    override suspend fun createNotification(notification: Notification): Boolean {
        return notifications.insertOne(notification).wasAcknowledged()
    }

    override suspend fun deleteNotification(notificationId: String): Boolean {
        return notifications.deleteOneById(notificationId).wasAcknowledged()
    }
}