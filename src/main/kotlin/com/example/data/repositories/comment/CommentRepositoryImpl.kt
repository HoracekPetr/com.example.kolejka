package com.example.data.repositories.comment

import com.example.data.models.Comment
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class CommentRepositoryImpl(
    db: CoroutineDatabase
) : CommentRepository {

    private val comments = db.getCollection<Comment>()

    override suspend fun createComment(comment: Comment): String {
        comments.insertOne(comment)
        return comment.id
    }

    override suspend fun deleteComment(commentId: String): Boolean {
        return comments.deleteOneById(commentId).wasAcknowledged()
    }

    override suspend fun deleteCommentsForPost(postId: String) {
        comments.deleteMany(Comment::postId eq postId)
    }

    override suspend fun getCommentsForPost(postId: String): List<Comment> {
        return comments.find(Comment::postId eq postId).toList()
    }

    override suspend fun getComment(commentId: String): Comment? {
        return comments.findOneById(commentId)
    }
}