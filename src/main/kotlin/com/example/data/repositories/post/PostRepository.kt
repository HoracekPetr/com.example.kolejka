package com.example.data.repositories.post

import com.example.data.models.Post
import com.example.util.Constants.PAGE_SIZE

interface PostRepository {

    suspend fun createPost(post: Post)

    suspend fun getPostsByAll(
        page: Int = 0,
        pageSize: Int = PAGE_SIZE
    ): List<Post>

    suspend fun getPostById(id: String): Post?

    suspend fun getPostsByMembers(userId: String): List<Post>

    suspend fun getPostsByCreator(userId: String): List<Post>

    suspend fun deletePost(id: String)
}