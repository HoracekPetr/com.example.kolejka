package com.example.routes

import com.example.data.requests.AddMemberRequest
import com.example.data.requests.CreatePostRequest
import com.example.data.requests.DeletePostRequest
import com.example.data.responses.BasicApiResponse
import com.example.data.util.NotificationAction
import com.example.data.util.PostType
import com.example.service.CommentService
import com.example.service.NotificationService
import com.example.service.PostService
import com.example.util.Constants
import com.example.util.QueryParameters
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.createPost(
    postService: PostService
) {
    authenticate {
        route("/api/post/create") {
            post {
                val request = call.receiveOrNull<CreatePostRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                val userId = call.userId

                postService.createPost(request, userId)

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
                    call.parameters[QueryParameters.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.POSTS_PAGE_SIZE

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
    commentService: CommentService
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

                if (post.userId == call.userId) {

                    postService.deletePost(request.postId)
                    commentService.deleteCommentsForPost(request.postId)

                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse(
                            successful = true
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.Unauthorized
                    )
                }
            }
        }
    }
}

fun Route.getPostsByCreator(
    postService: PostService
) {
    authenticate {
        route("/api/post/getPostsByCreator") {
            get {

                val postsByCreator = postService.getPostsByCreator(call.userId)

                call.respond(
                    HttpStatusCode.OK, postsByCreator
                )
            }
        }
    }
}

fun Route.getPostsWhereUserIsMember(
    postService: PostService
) {
    authenticate {
        route("/api/post/getPostsWhereMember") {
            get {


                val postsWhereUserIsMember = postService.getPostsWhereUserIsMember(call.userId)

                call.respond(
                    HttpStatusCode.OK, postsWhereUserIsMember
                )
            }
        }
    }
}

fun Route.addPostMember(
    postService: PostService,
    notificationService: NotificationService
) {
    authenticate {
        route("/api/post/addPostMember") {
            post {
                val request = call.receiveOrNull<AddMemberRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                val post = postService.getPost(request.postId)

                if (post != null) {
                    if (!postService.isPostMember(request.postId, call.userId)) {
                        if (postService.addPostMember(request.postId, call.userId)) {

                            notificationService.addPostNotification(
                                byUserId = call.userId,
                                postId = request.postId,
                                postType = PostType.fromType(post.type)
                            )


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
                    } else if (postService.isPostMember(request.postId, call.userId) && post.userId == call.userId) {
                        call.respond(
                            HttpStatusCode.Conflict,
                            BasicApiResponse(
                                message = "You're an owner of this post!",
                                successful = false
                            )
                        )
                    } else {
                        postService.removePostMember(request.postId, call.userId)
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse(
                                message = "User removed from the post member list.",
                                successful = true
                            )
                        )
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
    }
}


