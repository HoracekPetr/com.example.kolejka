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
                members = emptyList(),
                isEvent = request.isEvent,
                isOffer = request.isOffer
            )
        )
    }
}