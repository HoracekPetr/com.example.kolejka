package com.example.data.repositories.notification

import com.example.data.models.*
import com.example.data.models.push_notification.PushNotification
import com.example.data.responses.NotificationResponse
import com.example.data.util.OneSignalObjects
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.insertOne
import java.lang.Exception

class NotificationRepositoryImpl(
    db: CoroutineDatabase,
) : NotificationRepository {

    private val notifications = db.getCollection<Notification>()
    private val notificationsCount = db.getCollection<NotificationCount>()
    private val users = db.getCollection<User>()

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

    override suspend fun createNotificationCount(notificationCount: NotificationCount): Boolean {
        return notificationsCount.insertOne(notificationCount).wasAcknowledged()
    }

    override suspend fun deleteNotification(notificationId: String): Boolean {
        return notifications.deleteOneById(notificationId).wasAcknowledged()
    }

    override suspend fun deleteNotificationsForPost(postId: String): Boolean {
        return notifications.deleteMany(Notification::parentID eq postId).wasAcknowledged()
    }

    override suspend fun deleteAllNotificationsForUser(userId: String): Boolean {
        return notifications.deleteMany(Notification::toUserID eq userId).wasAcknowledged()
    }

    override suspend fun getNotificationCount(userId: String): Int {
        val count = notificationsCount.findOne(NotificationCount::userId eq userId)
        return count?.count ?: 0
    }

    override suspend fun updateNotificationCount(userId: String, notificationCount: NotificationCount): Boolean {

        val count = notificationsCount.findOne(NotificationCount::userId eq userId)
        return if (count != null) {
            notificationsCount.updateOneById(count.id, inc(count::count, 1)).wasAcknowledged()
        } else {
            notificationsCount.insertOne(notificationCount).wasAcknowledged()
        }
    }

    override suspend fun setNotificationsToZero(userId: String): Boolean {
        val count = notificationsCount.findOne(NotificationCount::userId eq userId)

        if (count != null) {
            val countValue = count.count
            if(countValue != 0){
                return notificationsCount.updateOneById(count.id, inc(count::count, -countValue)).wasAcknowledged()
            }

            return true
        }


        return false
    }

    override suspend fun updateNotificationInfo(userId: String): Boolean {
        return notifications.updateMany(
            filter = Post::userId eq userId,
            updates = arrayOf(
                SetTo(Notification::username, users.findOneById(userId)?.username)
            )
        ).wasAcknowledged()
    }

}