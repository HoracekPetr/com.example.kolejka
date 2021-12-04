package com.example.data.repositories.post

import com.example.data.models.Post
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase

class PostRepositoryImpl(
    db: CoroutineDatabase
):PostRepository {

    private val posts = db.getCollection<Post>()


    override suspend fun createPost(post: Post) {
        posts.insertOne(post)
    }


    override suspend fun getPostById(postId: String): Post? {
        return posts.findOneById(postId)
    }

    override suspend fun getPostsByAll(page: Int, pageSize: Int): List<Post> {
        return posts.find().skip(page*pageSize).limit(pageSize).toList()
    }

    override suspend fun getPostsWhereUserIsMember(userId: String): List<Post> {
        return posts.find(and(Post::members contains userId, Post::userId ne userId)).toList()
    }

    override suspend fun getPostsByCreator(userId: String): List<Post> {
        return posts.find(Post::userId eq userId).toList()
    }

    override suspend fun deletePost(postId: String) {
        posts.deleteOneById(postId)
    }

    override suspend fun addPostMember(postId: String, userId: String):Boolean {
        val postMembers = posts.findOneById(postId)?.members ?: return false
        return posts.updateOneById(postId, setValue(Post::members, postMembers + userId)).wasAcknowledged()
    }

    override suspend fun isPostMember(postId: String, userId: String): Boolean {
        val post = posts.findOneById(postId) ?: return false
        return userId in post.members
    }
}