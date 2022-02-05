package com.example.data.repositories.notification

import com.example.data.models.Notification
import com.example.data.models.NotificationCount
import com.example.data.models.User
import com.example.data.responses.NotificationResponse
import org.litote.kmongo.MongoOperator
import org.litote.kmongo.`in`
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.insertOne
import org.litote.kmongo.eq
import org.litote.kmongo.inc

class NotificationRepositoryImpl(
    db: CoroutineDatabase
) : NotificationRepository {

    private val notifications = db.getCollection<Notification>()
    private val notificationsCount = db.getCollection<NotificationCount>()

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

    override suspend fun getNotificationCount(userId: String): Int {
        val count =  notificationsCount.findOne(NotificationCount::userId eq userId)
        println("USERID: $userId")
        println("Notification count: ${NotificationCount::userId}")
        println("COUNT: $count")
        return count?.count ?: 10
    }

    override suspend fun updateNotificationCount(userId: String, notificationCount: NotificationCount): Boolean {

        val count = notificationsCount.findOne(NotificationCount::userId eq userId)
        println("COUNT: $count")
        return if (count != null) {
            println("UPDATUJU!!!!!!!!!!!!!!!!!!!!!")
            notificationsCount.updateOneById(count.id, inc(count::count, 1)).wasAcknowledged()
        } else {
            println("PŘIDÁVÁM!!!!!!!!!!!!!!!!!!!!!")
            notificationsCount.insertOne(notificationCount).wasAcknowledged()
        }
    }
}