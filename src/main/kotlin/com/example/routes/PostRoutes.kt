package com.example.routes

import com.example.data.models.Post
import com.example.data.repositories.post.PostRepository
import com.example.data.requests.CreateAccountRequest
import com.example.data.requests.CreatePostRequest
import com.example.data.responses.BasicApiResponse
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.createPostRoute(postRepository: PostRepository){
    route("/api/post/create"){
        post {
            val request = call.receiveOrNull<CreatePostRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

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

            call.respond(
                HttpStatusCode.OK,
                BasicApiResponse(
                    successful = true
                )
            )
        }
    }
}