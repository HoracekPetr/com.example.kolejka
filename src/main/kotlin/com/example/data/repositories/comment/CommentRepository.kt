package com.example.data.repositories.comment

import com.example.data.models.Comment

interface CommentRepository {

    suspend fun createComment(comment: Comment): Boolean

    suspend fun deleteComment(commentId: String): Boolean

    suspend fun deleteCommentsForPost(postId: String)

    suspend fun deleteCommentsFromUser(userId: String, postId: String)

    suspend fun getCommentsForPost(postId: String): List<Comment>

    suspend fun getComment(commentId: String): Comment?

    suspend fun updateCommentInfo(userId: String): Boolean
}