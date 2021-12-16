package com.example.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.requests.CreateAccountRequest
import com.example.data.requests.LoginRequest
import com.example.data.requests.UpdateProfileRequest
import com.example.data.responses.AuthResponse
import com.example.data.responses.BasicApiResponse
import com.example.service.UserService
import com.example.util.ApiResponseMessages
import com.example.util.ApiResponseMessages.FIELDS_BLANK
import com.example.util.ApiResponseMessages.INVALID_CREDENTIALS
import com.example.util.ApiResponseMessages.USER_ALREADY_EXISTS
import com.example.util.Constants.PROFILE_PIC_PATH
import com.example.util.Constants.PROFILE_PIC_URL
import com.example.util.QueryParameters
import com.example.util.validation.ValidationEvent
import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.auth.*
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

fun Route.createUser(
    userService: UserService
) {

    route("/api/user/create") {
        post {
            val request = call.receiveOrNull<CreateAccountRequest>() ?: kotlin.run {
                call.respond(BadRequest)
                return@post
            }

            if (userService.doesUserWithEmailExist(request.email)) {
                call.respond(
                    BasicApiResponse(USER_ALREADY_EXISTS, false)
                )
                return@post
            }

            when (userService.validateCreateAccountRequest(request)) {
                is ValidationEvent.EmptyFieldError -> {
                    call.respond(
                        BasicApiResponse(FIELDS_BLANK, false)
                    )
                    return@post
                }

                is ValidationEvent.Success -> {
                    userService.createUser(request)
                    call.respond(
                        OK,
                        BasicApiResponse(successful = true)
                    )
                }
            }
        }
    }
}

fun Route.loginUser(
    userService: UserService,
    jwtIssuer: String,
    jwtAudience: String,
    jwtSecret: String
) {

    route("/api/user/login") {
        post {
            val request = call.receiveOrNull<LoginRequest>() ?: kotlin.run {
                call.respond(BadRequest)
                return@post
            }

            when (userService.validateLoginRequest(request)) {

                is ValidationEvent.EmptyFieldError -> {
                    call.respond(
                        BasicApiResponse(FIELDS_BLANK, false)
                    )
                    return@post
                }

                is ValidationEvent.Success -> {

                    val user = userService.getUserByEmail(request.email) ?: kotlin.run {
                        call.respond(
                            OK,
                            BasicApiResponse(message = INVALID_CREDENTIALS, successful = false)
                        )
                        return@post
                    }

                    if (userService.isPasswordCorrect(request.email, user.password)) {

                        val expiresIn = 1000L * 60L * 60L * 24L * 365L //token expiruje za rok

                        val token = JWT.create()
                            .withClaim("userId", user.id)
                            .withIssuer(jwtIssuer)
                            .withExpiresAt(Date(System.currentTimeMillis() + expiresIn))
                            .withAudience(jwtAudience)
                            .sign(Algorithm.HMAC256(jwtSecret))

                        call.respond(
                            OK,
                            AuthResponse(token = token)
                        )
                    } else {
                        call.respond(
                            OK,
                            BasicApiResponse(message = INVALID_CREDENTIALS, successful = false)
                        )
                    }
                }
            }
        }
    }
}

fun Route.getUserProfile(
    userService: UserService
) {
    authenticate {
        route("/api/user/profile") {
            get {
                val userId = call.parameters[QueryParameters.USER_ID]
                if (userId == null || userId.isBlank()) {
                    BadRequest
                    return@get
                }

                val userProfile = userService.getUserProfile(userId)
                if (userProfile == null) {
                    OK
                    BasicApiResponse(
                        message = ApiResponseMessages.USER_NOT_FOUND,
                        successful = false
                    )
                    return@get
                }

                call.respond(
                    OK,
                    userProfile
                )
            }
        }
    }
}

fun Route.updateUserProfile(
    userService: UserService
) {

    val gson: Gson by inject()

    authenticate {
        route("/api/user/update") {
            put {
                val multipart = call.receiveMultipart()
                var updateProfileRequest: UpdateProfileRequest? = null
                var fileName: String? = null

                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            if (part.name == "update_profile_data") {
                                updateProfileRequest = gson.fromJson(part.value, UpdateProfileRequest::class.java)
                            }
                        }

                        is PartData.FileItem -> {
                            fileName = part.save(PROFILE_PIC_PATH)
                        }

                        is PartData.BinaryItem -> Unit
                    }
                    part.dispose
                }


                val profilePictureUrl = "${PROFILE_PIC_URL}/$fileName"

                updateProfileRequest?.let { request ->
                    val userUpdateAcknowledged = userService.updateUser(
                        userId = call.userId,
                        profilePictureUrl = profilePictureUrl,
                        updateProfileRequest = request
                    )
                    if(userUpdateAcknowledged){
                        call.respond(
                            OK,
                            BasicApiResponse(
                                successful = true
                            )
                        )
                    } else {
                        File("${PROFILE_PIC_PATH}/$fileName").delete()
                        call.respond(
                            InternalServerError
                        )
                    }
                } ?: kotlin.run{
                    call.respond(BadRequest)
                    return@put
                }
            }
        }
    }
}