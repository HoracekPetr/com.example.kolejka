package com.example.service

import com.example.data.models.Post
import com.example.data.repositories.post.PostRepository
import com.example.data.requests.CreatePostRequest

class PostService(
    private val postRepository: PostRepository
) {

    suspend fun createPost(request: CreatePostRequest){
        postRepository.createPost(
            Post(
                userId = request.userId,
                title = request.title,
                description = request.description,
                postImageUrl = request.postImageUrl,
                limit = request.limit,
                available = request.limit,
                members = mutableListOf(request.userId),
                isEvent = request.isEvent,
                isOffer = request.isOffer
            )
        )
    }

    suspend fun getPostsByAll(
        page: Int = 0,
        pageSize: Int = 15
    ): List<Post>{
        return postRepository.getPostsByAll(page, pageSize)
    }

    suspend fun getPost(postId: String): Post? = postRepository.getPostById(postId)
    suspend fun deletePost(postId: String) = postRepository.deletePost(postId)
}