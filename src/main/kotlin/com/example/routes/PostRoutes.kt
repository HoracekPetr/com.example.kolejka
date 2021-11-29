 package com.example.routes

import com.auth0.jwt.JWT
import com.example.data.models.Post
import com.example.data.repositories.post.PostRepository
import com.example.data.requests.CreateAccountRequest
import com.example.data.requests.CreatePostRequest
import com.example.data.responses.BasicApiResponse
import com.example.service.PostService
import com.example.service.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.createPostRoute(
    postService: PostService,
    userService: UserService
){
    authenticate {
        route("/api/post/create"){
            post {
                val request = call.receiveOrNull<CreatePostRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                val email = call.principal<JWTPrincipal>()?.getClaim("email", String::class)
                val isEmailByUser = userService.doesEmailBelongToUserId(
                    email = email ?: "",
                    userId = request.userId
                )

                if(!isEmailByUser){
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        "You are not authorized to execute this operation."
                    )
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
}