package com.example.data.repositories.comment

import com.example.data.models.Comment

interface CommentRepository {

    suspend fun createComment(comment: Comment): String

    suspend fun deleteComment(commentId: String): Boolean

    suspend fun deleteCommentsForPost(postId: String)

    suspend fun getCommentsForPost(postId: String): List<Comment>

    suspend fun getComment(commentId: String): Comment?
}