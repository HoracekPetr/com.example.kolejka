package com.example.routes

import com.example.data.requests.CreateCommentRequest
import com.example.data.requests.CreatePostRequest
import com.example.data.requests.DeleteCommentRequest
import com.example.data.responses.BasicApiResponse
import com.example.service.CommentService
import com.example.service.PostService
import com.example.service.UserService
import com.example.util.ApiResponseMessages
import com.example.util.CommentValidationEvent
import com.example.util.QueryParameters
import com.mongodb.client.model.ValidationAction
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.createComment(
    commentService: CommentService,
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
                                BasicApiResponse(
                                    successful = false,
                                    message = ApiResponseMessages.FIELDS_BLANK
                                )
                            )
                        }

                        is CommentValidationEvent.CommentTooLong -> {
                            call.respond(
                                HttpStatusCode.OK,
                                BasicApiResponse(
                                    successful = false,
                                    message = ApiResponseMessages.COMMENT_TOO_LONG
                                )
                            )

                        }

                        is CommentValidationEvent.Success -> {
                            call.respond(
                                HttpStatusCode.OK,
                                BasicApiResponse(
                                    successful = true
                                )
                            )
                        }
                    }
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        BasicApiResponse(
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
                        HttpStatusCode.BadRequest
                    )
                    return@get
                }

                val comments = commentService.getCommentsForPost(postId)
                call.respond(HttpStatusCode.OK, comments)
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
                val request = call.receiveOrNull<DeleteCommentRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }

                val comment = commentService.getComment(request.commentId)

                if (comment == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@delete
                }

                if (comment.userId == call.userId) {
                    commentService.deleteComment(request.commentId)

                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse(
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
