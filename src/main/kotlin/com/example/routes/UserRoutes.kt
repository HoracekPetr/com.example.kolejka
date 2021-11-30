package com.example.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.requests.CreateAccountRequest
import com.example.data.requests.LoginRequest
import com.example.data.responses.AuthResponse
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

            when(userService.validateLoginRequest(request)){
                is ValidationEvent.EmptyFieldError -> {
                    call.respond(
                        BasicApiResponse(FIELDS_BLANK, false)
                    )
                    return@post
                }

                is ValidationEvent.Success -> {
                    if (userService.isPasswordCorrect(request.email, request.password)) {

                        val expiresIn = 1000L*60L*60L*24L*365L //token expiruje za rok

                        val token = JWT.create()
                            .withClaim("email", request.email)
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