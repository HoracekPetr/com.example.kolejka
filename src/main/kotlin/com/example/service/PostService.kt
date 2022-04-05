package com.example.service

import com.example.data.models.Post
import com.example.data.repositories.post.PostRepository
import com.example.data.requests.CreatePostRequest
import com.example.data.requests.NewPostRequest

class PostService(
    private val postRepository: PostRepository
) {

    suspend fun  createNewPost(request: NewPostRequest, userId: String, username: String, profilePictureUrl: String): Boolean{
        return postRepository.createPost(
            Post(
                userId = userId,
                title = request.title,
                username = username,
                description = request.description,
                postPictureUrl = request.postImageURL,
                limit = request.limit,
                available = request.limit,
                members = mutableListOf(userId),
                type = request.type,
                profilePictureUrl = profilePictureUrl,
                date = request.date,
                location = request.location,
                timestamp = System.currentTimeMillis()
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
    suspend fun deletePost(postId: String): Boolean = postRepository.deletePost(postId)

    suspend fun getPostsByCreator(userId: String, page: Int, pageSize: Int): List<Post> = postRepository.getPostsByCreator(userId, page, pageSize)
    suspend fun getPostsWhereUserIsMember(userId: String, page: Int, pageSize: Int): List<Post> = postRepository.getPostsWhereUserIsMember(userId, page, pageSize)

    suspend fun addPostMember(postId: String, userId: String): Boolean = postRepository.addPostMember(postId, userId)
    suspend fun removePostMember(postId: String, userId: String): Boolean = postRepository.removePostMember(postId, userId)
    suspend fun isPostMember(postId: String, userId: String): Boolean = postRepository.isPostMember(postId, userId)

    suspend fun getPostById(postId: String): Post? = postRepository.getPostById(postId)

    suspend fun updatePostsProfilePic(userId: String): Boolean = postRepository.updatePostsInfo(userId)
}