package com.example.service

import com.example.data.models.Notification
import com.example.data.models.NotificationCount
import com.example.data.repositories.comment.CommentRepository
import com.example.data.repositories.notification.NotificationRepository
import com.example.data.repositories.post.PostRepository
import com.example.data.repositories.user.UserRepository
import com.example.data.responses.NotificationResponse
import com.example.data.util.NotificationAction
import com.example.data.util.PostType

class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    //private val commentRepository: CommentRepository
) {
    suspend fun getNotificationsForUser(userId: String, page: Int, pageSize: Int): List<NotificationResponse> =
        notificationRepository.getNotificationsForUser(userId, page, pageSize)

    suspend fun createNotification(notification: Notification) = notificationRepository.createNotification(notification)

    suspend fun deleteNotification(notificationId: String): Boolean =
        notificationRepository.deleteNotification(notificationId)

    suspend fun deleteNotificationsForPost(postId: String): Boolean = notificationRepository.deleteNotificationsForPost(postId)

    suspend fun addPostNotification(
        byUserId: String,
        postId: String,
        postType: PostType
    ) : Boolean {
        val toUserId = postRepository.getPostById(postId)?.userId ?: return false
        val username = userRepository.getUsernameById(byUserId) ?: ""

        when (postType) {
            PostType.Event -> {
                return notificationRepository.createNotification(
                    Notification(
                        byUserID = byUserId,
                        toUserID = toUserId,
                        parentID = postId,
                        timestamp = System.currentTimeMillis(),
                        type = NotificationAction.JoinedEvent.type,
                        username = username
                    )
                )
            }
            PostType.Offer -> {
                return notificationRepository.createNotification(
                    Notification(
                        byUserID = byUserId,
                        toUserID = toUserId,
                        parentID = postId,
                        timestamp = System.currentTimeMillis(),
                        type = NotificationAction.CalledDibs.type,
                        username = username
                    )
                )
            }
            else -> return false
        }
    }

    suspend fun addCommentNotification(
        byUserId: String,
        postId: String
    ): Boolean{

        val toUserId = postRepository.getPostById(postId)?.userId ?: return false
        val username = userRepository.getUsernameById(byUserId) ?: ""

        return notificationRepository.createNotification(
            Notification(
                byUserID = byUserId,
                toUserID = toUserId,
                timestamp = System.currentTimeMillis(),
                type = NotificationAction.CommentedOn.type,
                parentID = postId,
                username = username
            )
        )
    }

    suspend fun getNotificationCount(userId: String): Int = notificationRepository.getNotificationCount(userId)

    suspend fun updateNotificationCount(postId: String): Boolean {
        val toUserId = postRepository.getPostById(postId)?.userId ?: return false
        return notificationRepository.updateNotificationCount(toUserId, NotificationCount(userId = toUserId, count = 0))
    }

    suspend fun setNotificationsToZero(userId: String): Boolean {
        return notificationRepository.setNotificationsToZero(userId)
    }

    suspend fun updateNotificationInfo(userId: String): Boolean = notificationRepository.updateNotificationInfo(userId)
}