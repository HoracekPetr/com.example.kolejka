 package com.example.routes

import com.example.data.models.Post
import com.example.data.repositories.post.PostRepository
import com.example.data.requests.CreateAccountRequest
import com.example.data.requests.CreatePostRequest
import com.example.data.responses.BasicApiResponse
import com.example.service.PostService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.createPostRoute(postService: PostService){
    route("/api/post/create"){
        post {
            val request = call.receiveOrNull<CreatePostRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            postService.createPost(request)

            call.respond(
                HttpStatusCode.OK,
                BasicApiResponse(
                    successful = true
                )
            )
        }
    }
}