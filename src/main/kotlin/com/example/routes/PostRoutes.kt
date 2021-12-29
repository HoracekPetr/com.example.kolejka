package com.example.routes

import com.example.data.requests.AddMemberRequest
import com.example.data.requests.CreatePostRequest
import com.example.data.requests.DeletePostRequest
import com.example.data.requests.UpdateProfileRequest
import com.example.data.responses.BasicApiResponse
import com.example.data.util.NotificationAction
import com.example.data.util.PostType
import com.example.service.CommentService
import com.example.service.NotificationService
import com.example.service.PostService
import com.example.util.Constants
import com.example.util.Constants.POST_PIC_PATH
import com.example.util.Constants.POST_PIC_URL
import com.example.util.Constants.PROFILE_PIC_URL
import com.example.util.QueryParameters
import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import java.io.File
import java.util.*

fun Route.createPost(
    postService: PostService
) {

    val gson: Gson by inject()

    authenticate {
        route("/api/post/create") {
            post {
                val multipart = call.receiveMultipart()
                var createPostRequest: CreatePostRequest? = null
                var fileName: String? = null

                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            if (part.name == "create_post_data") {
                                createPostRequest = gson.fromJson(part.value, CreatePostRequest::class.java)
                            }
                        }

                        is PartData.FileItem -> {
                            fileName = part.save(POST_PIC_PATH)
                        }

                        is PartData.BinaryItem -> Unit
                    }
                    part.dispose
                }

                val postPictureURL = "${POST_PIC_URL}/$fileName"
                val userId = call.userId

                createPostRequest?.let { request ->
                    val postCreatedAcknowledged = postService.createPost(
                        request = request,
                        postPictureUrl = postPictureURL,
                        userId = userId
                    )

                    if (postCreatedAcknowledged) {
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse<Unit>(
                                successful = true
                            )
                        )
                    } else {
                        File("${POST_PIC_PATH}/$fileName").delete()
                        call.respond(
                            HttpStatusCode.InternalServerError
                        )
                    }
                } ?: kotlin.run {
                    call.respond(
                        HttpStatusCode.BadRequest
                    )
                    return@post
                }
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
                        BasicApiResponse<Unit>(
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
                val page = call.parameters[QueryParameters.PARAM_PAGE]?.toIntOrNull() ?: 0
                val pageSize =
                    call.parameters[QueryParameters.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.POSTS_PAGE_SIZE

                val postsByCreator = postService.getPostsByCreator(
                    userId = call.userId,
                    page = page,
                    pageSize = pageSize
                )

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
                val page = call.parameters[QueryParameters.PARAM_PAGE]?.toIntOrNull() ?: 0
                val pageSize =
                    call.parameters[QueryParameters.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.POSTS_PAGE_SIZE

                val postsWhereUserIsMember = postService.getPostsWhereUserIsMember(
                    userId = call.userId,
                    page = page,
                    pageSize = pageSize
                )

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
                                BasicApiResponse<Unit>(
                                    message = "User added to post member list.",
                                    successful = true
                                )
                            )

                        } else {
                            call.respond(
                                HttpStatusCode.BadRequest,
                                BasicApiResponse<Unit>(
                                    message = "Post doesn't exist!",
                                    successful = false
                                )
                            )
                        }
                    } else if (postService.isPostMember(request.postId, call.userId) && post.userId == call.userId) {
                        call.respond(
                            HttpStatusCode.Conflict,
                            BasicApiResponse<Unit>(
                                message = "You're an owner of this post!",
                                successful = false
                            )
                        )
                    } else {
                        postService.removePostMember(request.postId, call.userId)
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse<Unit>(
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


