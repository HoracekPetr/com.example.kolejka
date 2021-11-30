package com.example.routes

import com.example.data.requests.CreateCommentRequest
import com.example.data.requests.CreatePostRequest
import com.example.data.requests.DeleteCommentRequest
import com.example.data.responses.BasicApiResponse
import com.example.plugins.email
import com.example.service.CommentService
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
    userService: UserService
) {

    authenticate {
        route("/api/comment/create") {
            post {
                val request = call.receiveOrNull<CreateCommentRequest>() ?: kotlin.run {
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

                when (commentService.createComment(request)) {
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
    commentService: CommentService,
    userService: UserService
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

                val isEmailByUser = userService.doesEmailBelongToUserId(
                    email = call.principal<JWTPrincipal>()?.email ?: "",
                    userId = comment.userId
                )

                if (!isEmailByUser) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        "You are not authorized to execute this operation."
                    )
                    return@delete
                }

                commentService.deleteComment(request.commentId)

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
