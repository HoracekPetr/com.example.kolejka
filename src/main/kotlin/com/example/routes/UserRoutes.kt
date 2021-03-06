package com.example.routes

import com.example.data.requests.auth.ChangePasswordRequest
import com.example.data.requests.user.UpdateUserRequest
import com.example.data.responses.BasicApiResponse
import com.example.service.CommentService
import com.example.service.NotificationService
import com.example.service.PostService
import com.example.service.UserService
import com.example.util.ApiResponseMessages
import com.example.util.QueryParameters
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK

fun Route.getUserProfile(
    userService: UserService
) {
    authenticate {
        route("/api/user/profile") {
            get {
                //val userId = call.parameters[QueryParameters.USER_ID]
                val userId = call.userId
                if (userId.isBlank()) {
                    BadRequest
                    return@get
                }

                val userProfile = userService.getUserProfile(userId)
                if (userProfile == null) {
                    OK
                    BasicApiResponse<Unit>(
                        message = ApiResponseMessages.USER_NOT_FOUND,
                        successful = false
                    )
                    return@get
                }

                call.respond(
                    OK,
                    BasicApiResponse(
                        successful = true,
                        data = userProfile
                    )
                )
            }
        }
    }
}

fun Route.getOtherUserProfile(
    userService: UserService
) {
    authenticate {
        route("/api/user/other") {
            get {
                val userId = call.parameters[QueryParameters.USER_ID] ?: kotlin.run {
                    call.respond(BadRequest)
                    return@get
                }

                val userProfile = userService.getUserProfile(userId)
                if (userProfile == null) {
                    OK
                    BasicApiResponse<Unit>(
                        message = ApiResponseMessages.USER_NOT_FOUND,
                        successful = false
                    )
                    return@get
                }

                call.respond(
                    OK,
                    BasicApiResponse(
                        successful = true,
                        data = userProfile
                    )
                )
            }
        }
    }
}

fun Route.updateUserInfo(
    userService: UserService,
    postService: PostService,
    commentService: CommentService,
    notificationService: NotificationService
) {
    authenticate {
        route("/api/user/update2") {
            put {

                val request = call.receiveOrNull<UpdateUserRequest>() ?:kotlin.run {
                    call.respond(BadRequest)
                    return@put
                }


                val updateProfile = userService.updateUserInfo(
                    userId = call.userId,
                    updateUserRequest = request
                )

                if(updateProfile){
                    postService.updatePostsProfilePic(call.userId)
                    commentService.updateCommentInfo(call.userId)
                    notificationService.updateNotificationInfo(call.userId)
                    call.respond(
                        OK,
                        BasicApiResponse<Unit>(
                            successful = true
                        )
                    )
                } else {
                    call.respond(
                        BadRequest,
                        BasicApiResponse<Unit>(
                            successful = false
                        )
                    )
                    return@put
                }
            }
        }
    }
}

fun Route.changeUserPassword(
    userService: UserService
){
    route("/api/user/change"){
        put{
            val request = call.receiveOrNull<ChangePasswordRequest>() ?: kotlin.run {
                BadRequest
                return@put
            }

            when(userService.changeUserPassword(request)){
                true -> {
                    call.respond(
                        OK,
                        BasicApiResponse<Unit>(
                            successful = true
                        )
                    )
                }
                false -> {
                    call.respond(
                        BadRequest,
                        BasicApiResponse<Unit>(
                            successful = false
                        )
                    )
                }
            }

        }
    }
}

fun Route.getUserId(
    userService: UserService
){
    route("/api/user/id"){
        get {
            val userEmail = call.parameters[QueryParameters.EMAIL] ?: kotlin.run {
                BadRequest
                return@get
            }

            val userId = userService.getUserByEmail(userEmail)?.id

            if(userId == null){
                call.respond(
                    NotFound,
                    BasicApiResponse<Unit>(
                        successful = false
                    )
                )
                return@get
            }

            call.respond(
                OK,
                BasicApiResponse(
                    successful = true,
                    data = userId
                )
            )
        }
    }
}
