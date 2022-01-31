package com.example.data.repositories.notification

import com.example.data.models.Notification
import com.example.data.models.User
import com.example.data.responses.NotificationResponse
import org.litote.kmongo.`in`
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.insertOne
import org.litote.kmongo.eq

class NotificationRepositoryImpl(
    db: CoroutineDatabase
) : NotificationRepository {

    private val users = db.getCollection<User>()
    private val notifications = db.getCollection<Notification>()

    override suspend fun getNotificationsForUser(userId: String, page: Int, pageSize: Int): List<NotificationResponse> {
        val notifications = notifications.find(Notification::toUserID eq userId)
            .skip(page * pageSize)
            .limit(pageSize)
            .descendingSort(Notification::timestamp)
            .toList()
        val usernames = notifications.map { it.username }
        println("Usernames: $usernames")
        return notifications.mapIndexed { i, notification ->
            println("Index: $i")
            NotificationResponse(
                timestamp = notification.timestamp,
                parentId = notification.parentID,
                type = notification.type,
                username = usernames[i],
                id = notification.id
            )
        }
    }

    override suspend fun createNotification(notification: Notification): Boolean {
        return notifications.insertOne(notification).wasAcknowledged()
    }

    override suspend fun deleteNotification(notificationId: String): Boolean {
        return notifications.deleteOneById(notificationId).wasAcknowledged()
    }
}