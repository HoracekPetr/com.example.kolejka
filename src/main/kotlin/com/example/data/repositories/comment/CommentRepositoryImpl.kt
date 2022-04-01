package com.example.data.repositories.comment

import com.example.data.models.Comment
import com.example.data.models.Post
import com.example.data.models.User
import org.litote.kmongo.SetTo
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class CommentRepositoryImpl(
    db: CoroutineDatabase
) : CommentRepository {

    private val comments = db.getCollection<Comment>()
    private val users = db.getCollection<User>()

    override suspend fun createComment(comment: Comment): Boolean {
        return comments.insertOne(comment).wasAcknowledged()
    }

    override suspend fun deleteComment(commentId: String): Boolean {
        return comments.deleteOneById(commentId).wasAcknowledged()
    }

    override suspend fun deleteCommentsForPost(postId: String) {
        comments.deleteMany(Comment::postId eq postId)
    }

    override suspend fun deleteCommentsFromUser(userId: String, postId: String) {
        comments.deleteMany(Comment::postId eq postId, Comment::userId eq userId)
    }

    override suspend fun getCommentsForPost(postId: String): List<Comment> {
        return comments.find(Comment::postId eq postId).toList()
    }

    override suspend fun getComment(commentId: String): Comment? {
        return comments.findOneById(commentId)
    }

    override suspend fun updateCommentInfo(userId: String): Boolean {
        return comments.updateMany(filter = Comment::userId eq userId,
            updates = arrayOf(
                SetTo(Comment::profilePictureUrl, users.findOneById(userId)?.profilePictureURL),
                SetTo(Comment::username, users.findOneById(userId)?.username)
            )).wasAcknowledged()
    }
}