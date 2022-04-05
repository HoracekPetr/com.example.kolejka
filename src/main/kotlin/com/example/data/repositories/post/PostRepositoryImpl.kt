package com.example.data.repositories.post

import com.example.data.models.Post
import com.example.data.models.User
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase

class PostRepositoryImpl(
    db: CoroutineDatabase
) : PostRepository {

    private val posts = db.getCollection<Post>()
    private val users = db.getCollection<User>()

    override suspend fun createPost(post: Post): Boolean {
        return posts.insertOne(post).wasAcknowledged()
    }

    override suspend fun getPostById(postId: String): Post? {
        return posts.findOneById(postId)
    }

    override suspend fun getPostsByAll(page: Int, pageSize: Int): List<Post> {

        return posts.find(Post::available gt 0).skip(page * pageSize).limit(pageSize).ascendingSort().toList()

    }

    override suspend fun updatePostsInfo(userId: String): Boolean {
        return posts.updateMany(filter = Post::userId eq userId,
            updates = arrayOf(
                SetTo(Post::profilePictureUrl, users.findOneById(userId)?.profilePictureURL),
                SetTo(Post::username, users.findOneById(userId)?.username)
            )).wasAcknowledged()
    }

    override suspend fun getPostsWhereUserIsMember(userId: String, page: Int, pageSize: Int): List<Post> {
        return posts.find(and(Post::members contains userId, Post::userId ne userId))
            .skip(page * pageSize)
            .limit(pageSize)
            .toList()
    }

    override suspend fun getPostsByCreator(userId: String, page: Int, pageSize: Int): List<Post> {
        return posts.find(Post::userId eq userId)
            .skip(page * pageSize)
            .limit(pageSize)
            .toList()
    }

    override suspend fun deletePost(postId: String): Boolean {
        return posts.deleteOneById(postId).wasAcknowledged()
    }

    override suspend fun addPostMember(postId: String, userId: String): Boolean {
        val postMembers = posts.findOneById(postId)?.members ?: return false
        posts.updateOneById(postId, inc(Post::available, -1))
        return posts.updateOneById(postId, setValue(Post::members, postMembers + userId)).wasAcknowledged()
    }

    override suspend fun removePostMember(postId: String, userId: String): Boolean {
        val postMembers = posts.findOneById(postId)?.members ?: return false
        posts.updateOneById(postId, inc(Post::available, 1))
        return posts.updateOneById(postId, setValue(Post::members, postMembers - userId)).wasAcknowledged()
    }

    override suspend fun isPostMember(postId: String, userId: String): Boolean {
        val post = posts.findOneById(postId) ?: return false
        return userId in post.members
    }
}