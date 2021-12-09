package com.example.service

import com.example.data.models.Comment
import com.example.data.repositories.comment.CommentRepository
import com.example.data.requests.CreateCommentRequest
import com.example.util.CommentValidationEvent
import com.example.util.Constants

class CommentService(
    private val commentRepository: CommentRepository
) {

    suspend fun createComment(createCommentRequest: CreateCommentRequest, userId: String): CommentValidationEvent {
        createCommentRequest.apply {
            if (comment.isBlank() || postId.isBlank()) {
                CommentValidationEvent.EmptyFieldError
            }
            if(comment.length > Constants.MAX_COMMENT_LENGTH){
                CommentValidationEvent.CommentTooLong
            }
        }
        val commentId = commentRepository.createComment(
            Comment(
                userId = userId,
                postId = createCommentRequest.postId,
                comment = createCommentRequest.comment,
                timestamp = System.currentTimeMillis()
            )
        )
        return CommentValidationEvent.Success(commentId = commentId)
    }

    suspend fun getCommentsForPost(postId: String): List<Comment>{
        return commentRepository.getCommentsForPost(postId)
    }

    suspend fun deleteCommentsForPost(postId: String) = commentRepository.deleteCommentsForPost(postId)

    suspend fun deleteComment(commentId: String): Boolean = commentRepository.deleteComment(commentId)


    suspend fun getComment(commentId: String): Comment? = commentRepository.getComment(commentId)

}