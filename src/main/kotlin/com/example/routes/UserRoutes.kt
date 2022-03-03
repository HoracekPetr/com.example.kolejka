package com.example.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.requests.RegisterAccountRequest
import com.example.data.requests.LoginAccountRequest
import com.example.data.requests.UpdateProfileRequest
import com.example.data.requests.UpdateUserRequest
import com.example.data.responses.AuthResponse
import com.example.data.responses.BasicApiResponse
import com.example.service.PostService
import com.example.service.UserService
import com.example.util.ApiResponseMessages
import com.example.util.ApiResponseMessages.FIELDS_BLANK
import com.example.util.ApiResponseMessages.INVALID_CREDENTIALS
import com.example.util.ApiResponseMessages.USER_ALREADY_EXISTS
import com.example.util.Constants.BASE_URL
import com.example.util.Constants.PROFILE_PIC_PATH
import com.example.util.QueryParameters
import com.example.util.validation.ValidationEvent
import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.content.*
import org.koin.ktor.ext.inject
import java.io.File
import java.util.*

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

fun Route.updateUserInfo(
    userService: UserService,
    postService: PostService
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
