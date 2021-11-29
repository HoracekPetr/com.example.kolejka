package com.example.routes

import com.example.data.repositories.user.UserRepository
import com.example.data.models.User
import com.example.data.requests.CreateAccountRequest
import com.example.data.requests.LoginRequest
import com.example.data.responses.BasicApiResponse
import com.example.service.UserService
import com.example.util.ApiResponseMessages.FIELDS_BLANK
import com.example.util.ApiResponseMessages.INVALID_CREDENTIALS
import com.example.util.ApiResponseMessages.USER_ALREADY_EXISTS
import com.example.util.ValidationEvent
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import org.koin.ktor.ext.inject

fun Route.createUserRoute(
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

fun Route.loginUserRoute(userService: UserService) {

    route("/api/user/login") {
        post {
            val request = call.receiveOrNull<LoginRequest>() ?: kotlin.run {
                call.respond(BadRequest)
                return@post
            }

            when(userService.validateLoginRequest(request)){
                is ValidationEvent.EmptyFieldError -> {
                    call.respond(BadRequest)
                    return@post
                }

                is ValidationEvent.Success -> {
                    if (userService.isPasswordCorrect(request.email, request.password)) {
                        call.respond(
                            OK,
                            BasicApiResponse(successful = true)
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