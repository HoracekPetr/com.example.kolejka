package com.example.routes

import com.example.data.requests.AddMemberRequest
import com.example.data.requests.CreatePostRequest
import com.example.data.requests.NewPostRequest
import com.example.data.responses.BasicApiResponse
import com.example.data.responses.PostDetailResponse
import com.example.data.util.PostType
import com.example.service.CommentService
import com.example.service.NotificationService
import com.example.service.PostService
import com.example.service.UserService
import com.example.util.ApiResponseMessages.POST_NOT_FOUND
import com.example.util.Constants
import com.example.util.Constants.BASE_URL
import com.example.util.Constants.POST_PIC_PATH
import com.example.util.QueryParameters
import com.example.util.QueryParameters.POST_ID
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


fun Route.createNewPost(
    postService: PostService,
    userService: UserService,
) {
    authenticate {
        route("/api/post/new"){
            post {
                val request = call.receiveOrNull<NewPostRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                val username = userService.getUsernameById(call.userId)
                val profilePictureUrl = userService.getUserProfileUrl(call.userId)

                val newPost = postService.createNewPost(
                    request = request,
                    userId = call.userId,
                    username = username ?: "",
                    profilePictureUrl = profilePictureUrl ?: ""
                )

                if(newPost){
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse<Unit>(
                            successful = true
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        BasicApiResponse<Unit>(
                            successful = false
                        )
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

fun Route.getPostById(
    postService: PostService
) {
    authenticate {
        route("/api/post/get") {
            get {
                val postId = call.parameters[QueryParameters.PARAM_POST_ID]
                if (postId == null) {
                    call.respond(
                        status = HttpStatusCode.NotFound,
                        message = BasicApiResponse<Unit>(message = POST_NOT_FOUND, successful = false)
                    )
                } else {
                    val post = postService.getPostById(postId)
                    call.respond(
                        status = HttpStatusCode.OK,
/*                        message = BasicApiResponse(
                            successful = true,
                            data = post
                        )*/
                        message = BasicApiResponse<PostDetailResponse>(
                            successful = true,
                            data = PostDetailResponse(post = post, requesterId = call.userId)
                        )
                    )
                }
            }
        }
    }
}

fun Route.deletePost(
    postService: PostService,
    commentService: CommentService,
    notificationService: NotificationService
) {
    authenticate {
        route("/api/post/delete") {
            delete {
                val postId = call.parameters[POST_ID] ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }

                val post = postService.getPost(postId)

                if (post == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@delete
                }

                if (post.userId == call.userId) {

                    postService.deletePost(postId)
                    commentService.deleteCommentsForPost(postId)
                    notificationService.deleteNotificationsForPost(postId)

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
                val userId = call.userId
                val page = call.parameters[QueryParameters.PARAM_PAGE]?.toIntOrNull() ?: 0
                val pageSize =
                    call.parameters[QueryParameters.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.POSTS_PAGE_SIZE

                val postsByCreator = postService.getPostsByCreator(
                    userId = userId,
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

fun Route.getPostsByOtherCreator(
    postService: PostService
) {
    authenticate {
        route("/api/post/getPostsByOtherCreator") {
            get {
                val userId = call.parameters[QueryParameters.USER_ID] ?: ""
                val page = call.parameters[QueryParameters.PARAM_PAGE]?.toIntOrNull() ?: 0
                val pageSize =
                    call.parameters[QueryParameters.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.POSTS_PAGE_SIZE

                val postsByCreator = postService.getPostsByCreator(
                    userId = userId,
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
                val userId = call.userId
                val page = call.parameters[QueryParameters.PARAM_PAGE]?.toIntOrNull() ?: 0
                val pageSize =
                    call.parameters[QueryParameters.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.POSTS_PAGE_SIZE

                val postsWhereUserIsMember = postService.getPostsWhereUserIsMember(
                    userId = userId,
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
    notificationService: NotificationService,
    commentService: CommentService
) {
    authenticate {
        route("/api/post/addPostMember") {
            post {
                val request = call.receiveOrNull<AddMemberRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                val post = postService.getPost(request.postId ?: "")

                if (post != null) {
                    if (!postService.isPostMember(request.postId ?: "", call.userId)) {
                        if(post.available == 0){
                            call.respond(
                                HttpStatusCode.Forbidden,
                                BasicApiResponse<Unit>(
                                    message = "Post has reached its limit!",
                                    successful = false
                                )
                            )
                            return@post
                        }
                        if (postService.addPostMember(request.postId ?: "", call.userId)) {

                            notificationService.addPostNotification(
                                byUserId = call.userId,
                                postId = request.postId ?: "",
                                postType = PostType.fromType(post.type)
                            )

                            notificationService.updateNotificationCount(
                                postId = request.postId ?: ""
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
                    } else if (postService.isPostMember(request.postId ?: "", call.userId) && post.userId == call.userId) {
                        call.respond(
                            HttpStatusCode.Conflict,
                            BasicApiResponse<Unit>(
                                message = "You're an owner of this post!",
                                successful = false
                            )
                        )
                    }
                    else {

                        postService.removePostMember(request.postId ?: "", call.userId)
                        commentService.deleteCommentsFromUser(postId = post.id, userId = call.userId)


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


