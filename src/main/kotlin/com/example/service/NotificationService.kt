package com.example.service

import com.example.data.models.Notification
import com.example.data.repositories.comment.CommentRepository
import com.example.data.repositories.notification.NotificationRepository
import com.example.data.repositories.post.PostRepository
import com.example.data.util.NotificationAction
import com.example.data.util.PostType

class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository
) {
    suspend fun getNotificationsForUser(userId: String, page: Int, pageSize: Int): List<Notification> =
        notificationRepository.getNotificationsForUser(userId, page, pageSize)

    suspend fun createNotification(notification: Notification) = notificationRepository.createNotification(notification)

    suspend fun deleteNotification(notificationId: String): Boolean =
        notificationRepository.deleteNotification(notificationId)

    suspend fun addPostNotification(
        byUserId: String,
        postId: String,
        postType: PostType
    ) : Boolean {
        val toUserId = postRepository.getPostById(postId)?.userId ?: return false

        return notificationRepository.createNotification(
            Notification(
                byUserID = byUserId,
                toUserID = toUserId,
                parentID = postId,
                timestamp = System.currentTimeMillis(),
                type = postType.type
            )
        )
    }

    suspend fun addCommentNotification(
        byUserId: String,
        postId: String,
        commentId: String
    ): Boolean{

        val userIdOfPost = postRepository.getPostById(postId)?.userId ?: return false

        return notificationRepository.createNotification(
            Notification(
                byUserID = byUserId,
                toUserID = userIdOfPost,
                timestamp = System.currentTimeMillis(),
                type = NotificationAction.CommentedOn.type,
                parentID = commentId
            )
        )
    }
}