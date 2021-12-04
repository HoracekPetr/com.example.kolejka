package com.example.routes

import com.example.data.requests.AddMemberRequest
import com.example.data.requests.CreatePostRequest
import com.example.data.requests.DeletePostRequest
import com.example.data.responses.BasicApiResponse
import com.example.plugins.email
import com.example.service.PostService
import com.example.service.UserService
import com.example.util.Constants
import com.example.util.QueryParameters
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.createPost(
    postService: PostService,
    userService: UserService
) {
    authenticate {
        route("/api/post/create") {
            post {
                val request = call.receiveOrNull<CreatePostRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                val isEmailByUser = userService.doesEmailBelongToUserId(
                    email = call.principal<JWTPrincipal>()?.email ?: "",
                    userId = request.userId
                )

                if (!isEmailByUser) {
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

fun Route.getPostsByAll(
    postService: PostService
) {
    authenticate {
        route("/api/post/getPostsByAll") {
            get {
                val page = call.parameters[QueryParameters.PARAM_PAGE]?.toIntOrNull() ?: 0
                val pageSize =
                    call.parameters[QueryParameters.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.PAGE_SIZE

                val allPosts = postService.getPostsByAll(page, pageSize)
                call.respond(
                    HttpStatusCode.OK, allPosts
                )
            }
        }
    }
}

fun Route.deletePost(
    postService: PostService,
    userService: UserService
) {
    authenticate {
        route("/api/post/delete") {
            delete {
                val request = call.receiveOrNull<DeletePostRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }

                val post = postService.getPost(request.postId)

                if (post == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@delete
                }

                val isEmailByUser = userService.doesEmailBelongToUserId(
                    email = call.principal<JWTPrincipal>()?.email ?: "",
                    userId = post.userId
                )

                if (!isEmailByUser) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        "You are not authorized to execute this operation."
                    )
                    return@delete
                }

                postService.deletePost(request.postId)

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

fun Route.getPostsByCreator(
    postService: PostService,
    userService: UserService
) {
    authenticate {
        route("/api/post/getPostsByCreator") {
            get {
                val userId = call.parameters[QueryParameters.PARAM_USER_ID] ?: kotlin.run {
                    call.respond(
                        HttpStatusCode.BadRequest
                    )
                    return@get
                }

                val isEmailByUser = userService.doesEmailBelongToUserId(
                    email = call.principal<JWTPrincipal>()?.email ?: "",
                    userId = userId
                )

                if (!isEmailByUser) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        "You are not authorized to execute this operation."
                    )
                    return@get
                }

                val postsByCreator = postService.getPostsByCreator(userId)

                call.respond(
                    HttpStatusCode.OK, postsByCreator
                )
            }
        }
    }
}

fun Route.getPostsWhereUserIsMember(
    postService: PostService,
    userService: UserService
){
    authenticate {
        route("/api/post/getPostsWhereMember"){
            get {
                val userId = call.parameters[QueryParameters.PARAM_USER_ID] ?: kotlin.run {
                    call.respond(
                        HttpStatusCode.BadRequest
                    )
                    return@get
                }

                val isEmailByUser = userService.doesEmailBelongToUserId(
                    email = call.principal<JWTPrincipal>()?.email ?: "",
                    userId = userId
                )

                if (!isEmailByUser) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        "You are not authorized to execute this operation."
                    )
                    return@get
                }

                val postsWhereUserIsMember = postService.getPostsWhereUserIsMember(userId)

                call.respond(
                    HttpStatusCode.OK, postsWhereUserIsMember
                )
            }
        }
    }
}

fun Route.addPostMember(
    postService: PostService,
    userService: UserService
) {
    authenticate {
        route("/api/post/addPostMember") {
            post {
                val request = call.receiveOrNull<AddMemberRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                val isEmailByUser = userService.doesEmailBelongToUserId(
                    email = call.principal<JWTPrincipal>()?.email ?: "",
                    userId = request.userId
                )

                if (!isEmailByUser) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        "You are not authorized to execute this operation."
                    )
                    return@post
                }

                if (!postService.isPostMember(request.postId, request.userId)) {
                    if (postService.addPostMember(request.postId, request.userId)) {
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse(
                                message = "User added to post member list.",
                                successful = true
                            )
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            BasicApiResponse(
                                message = "Post doesn't exist!",
                                successful = false
                            )
                        )
                    }
                } else {
                    call.respond(
                        HttpStatusCode.Conflict,
                        BasicApiResponse(
                            message = "You are already member of this post!",
                            successful = false
                        )
                    )
                }
            }
        }
    }
}