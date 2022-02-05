package com.example.routes

import com.example.data.requests.CreateCommentRequest
import com.example.data.responses.BasicApiResponse
import com.example.service.CommentService
import com.example.service.NotificationService
import com.example.service.PostService
import com.example.util.ApiResponseMessages
import com.example.util.ApiResponseMessages.COMMENTS_NOT_FOUND
import com.example.util.validation.CommentValidationEvent
import com.example.util.QueryParameters
import com.example.util.QueryParameters.COMMENT_ID
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.createComment(
    commentService: CommentService,
    notificationService: NotificationService,
    postService: PostService
) {
    authenticate {
        route("/api/comment/create") {
            post {
                val request = call.receiveOrNull<CreateCommentRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                val post = postService.getPostById(request.postId) ?: kotlin.run {
                    call.respond(HttpStatusCode.NotFound)
                    return@post
                }

                if (call.userId in post.members) {
                    when (commentService.createComment(request, call.userId)) {

                        is CommentValidationEvent.EmptyFieldError -> {
                            call.respond(
                                HttpStatusCode.OK,
                                BasicApiResponse<Unit>(
                                    successful = false,
                                    message = ApiResponseMessages.FIELDS_BLANK
                                )
                            )
                        }

                        is CommentValidationEvent.CommentTooLong -> {
                            call.respond(
                                HttpStatusCode.OK,
                                BasicApiResponse<Unit>(
                                    successful = false,
                                    message = ApiResponseMessages.COMMENT_TOO_LONG
                                )
                            )

                        }

                        is CommentValidationEvent.Success -> {
                            call.respond(
                                HttpStatusCode.OK,
                                BasicApiResponse<Unit>(
                                    successful = true
                                )
                            )

                            if(call.userId != post.userId) {
                                notificationService.addCommentNotification(call.userId, request.postId)
                            }
                        }
                    }
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        BasicApiResponse<Unit>(
                            message = "You can't comment on post you are not a member of.",
                            successful = false
                        )
                    )
                }
            }
        }
    }

}

fun Route.getCommentsForPost(
    commentService: CommentService
) {
    authenticate {
        route("/api/comment/get") {
            get {
                val postId = call.parameters[QueryParameters.PARAM_POST_ID] ?: kotlin.run {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        BasicApiResponse<Unit>(
                            message = COMMENTS_NOT_FOUND,
                            successful = false
                        )
                    )
                    return@get
                }

                val comments = commentService.getCommentsForPost(postId)
                call.respond(HttpStatusCode.OK, BasicApiResponse(
                    successful = true,
                    data = comments
                ))
            }
        }
    }
}

fun Route.deleteComment(
    commentService: CommentService
) {
    authenticate {
        route("/api/comment/delete") {
            delete {
                val commentId = call.parameters[COMMENT_ID] ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }

                val comment = commentService.getComment(commentId)

                if (comment == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@delete
                }

                if (comment.userId == call.userId) {
                    commentService.deleteComment(commentId)

                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse<Unit>(
                            successful = true
                        )
                    )
                } else {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@delete
                }
            }
        }
    }
}
