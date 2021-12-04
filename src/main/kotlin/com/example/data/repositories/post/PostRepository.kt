package com.example.data.repositories.post

import com.example.data.models.Post
import com.example.util.Constants.PAGE_SIZE

interface PostRepository {

    suspend fun createPost(post: Post)

    suspend fun getPostsByAll(
        page: Int = 0,
        pageSize: Int = PAGE_SIZE
    ): List<Post>

    suspend fun getPostById(postId: String): Post?

    suspend fun getPostsWhereUserIsMember(userId: String): List<Post>

    suspend fun getPostsByCreator(userId: String): List<Post>

    suspend fun deletePost(postId: String)

    suspend fun addPostMember(postId: String, userId: String):Boolean

    suspend fun isPostMember(postId: String, userId: String): Boolean
}