package com.example.routes

import com.example.data.models.push_notification.PushNotification
import com.example.data.models.push_notification.PushNotificationMessage
import com.example.data.requests.post.AddMemberRequest
import com.example.data.requests.post.NewPostRequest
import com.example.data.requests.post.UpdatePostRequest
import com.example.data.responses.BasicApiResponse
import com.example.data.responses.PostDetailResponse
import com.example.data.util.OneSignalObjects
import com.example.data.util.PostType
import com.example.service.*
import com.example.util.ApiResponseMessages.DESC_TOO_LONG
import com.example.util.ApiResponseMessages.FIELDS_BLANK
import com.example.util.ApiResponseMessages.LIMIT_CANT_BE_LOWER
import com.example.util.ApiResponseMessages.POST_NOT_FOUND
import com.example.util.ApiResponseMessages.TITLE_TOO_LONG
import com.example.util.Constants
import com.example.util.QueryParameters
import com.example.util.QueryParameters.POST_ID
import com.example.util.validation.EditPostValidation
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*


fun Route.createNewPost(
    postService: PostService,
    userService: UserService,
    pushNotificationService: PushNotificationService,
    apiKey: String
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

                    pushNotificationService.sendPushNotification(
                        pushNotification = PushNotification(
                            includedSegments = listOf("All"),
                            heading = PushNotificationMessage(en = "Kolejka"),
                            contents = PushNotificationMessage(en = "There is a new post!"),
                            appId = OneSignalObjects.APP_ID
                        ),
                        apiKey = apiKey
                    )

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

fun Route.editPostInfo(
    postService: PostService
) {
    authenticate{
        route("/api/post/edit"){
            put {
                val request = call.receiveOrNull<UpdatePostRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@put
                }

                if(call.userId != postService.getPostById(request.postId)?.userId){
                    call.respond(
                        HttpStatusCode.Forbidden,
                        BasicApiResponse<Unit>(successful = false)
                    )
                }

                when(postService.editPostInfo(request)){
                    is EditPostValidation.EmptyFieldError -> {
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse<Unit>(message = FIELDS_BLANK, successful = false)
                        )
                    }
                    is EditPostValidation.TitleTooLong -> {
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse<Unit>(message = TITLE_TOO_LONG, successful = false)
                        )
                    }
                    is EditPostValidation.DescriptionTooLong -> {
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse<Unit>(message = DESC_TOO_LONG, successful = false)
                        )
                    }

                    is EditPostValidation.LimitCantBeLower -> {
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse<Unit>(message = LIMIT_CANT_BE_LOWER, successful = false)
                        )
                    }

                    is EditPostValidation.Success -> {
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse<Unit>(successful = true)
                        )
                    }
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


