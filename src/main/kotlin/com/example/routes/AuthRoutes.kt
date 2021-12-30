package com.example.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.requests.LoginAccountRequest
import com.example.data.requests.RegisterAccountRequest
import com.example.data.responses.AuthResponse
import com.example.data.responses.BasicApiResponse
import com.example.service.UserService
import com.example.util.ApiResponseMessages
import com.example.util.validation.ValidationEvent
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.util.*

fun Route.registerUser(
    userService: UserService
) {

    route("/api/user/create") {
        post {
            val request = call.receiveOrNull<RegisterAccountRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            if (userService.doesUserWithEmailExist(request.email)) {
                call.respond(
                    BasicApiResponse<Unit>(ApiResponseMessages.USER_ALREADY_EXISTS, false)
                )
                return@post
            }

            when (userService.validateCreateAccountRequest(request)) {
                is ValidationEvent.EmptyFieldError -> {
                    call.respond(
                        BasicApiResponse<Unit>(ApiResponseMessages.FIELDS_BLANK, false)
                    )
                    return@post
                }

                is ValidationEvent.Success -> {
                    userService.createUser(request)
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse<Unit>(successful = true)
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
            val request = call.receiveOrNull<LoginAccountRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            when (userService.validateLoginRequest(request)) {

                is ValidationEvent.EmptyFieldError -> {
                    call.respond(
                        BasicApiResponse<Unit>(ApiResponseMessages.FIELDS_BLANK, false)
                    )
                    return@post
                }

                is ValidationEvent.Success -> {

                    val user = userService.getUserByEmail(request.email) ?: kotlin.run {
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse<Unit>(message = ApiResponseMessages.INVALID_CREDENTIALS, successful = false)
                        )
                        return@post
                    }

                    if (userService.isPasswordCorrect(user.email, request.password)) {

                        val expiresIn = 1000L * 60L * 60L * 24L * 365L //token expiruje za rok

                        val token = JWT.create()
                            .withClaim("userId", user.id)
                            .withIssuer(jwtIssuer)
                            .withExpiresAt(Date(System.currentTimeMillis() + expiresIn))
                            .withAudience(jwtAudience)
                            .sign(Algorithm.HMAC256(jwtSecret))

                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse(
                                successful = true,
                                data = AuthResponse(token = token)
                            )
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse<Unit>(message = ApiResponseMessages.INVALID_CREDENTIALS, successful = false)
                        )
                    }
                }
            }
        }
    }
}

fun Route.authenticate(){
    authenticate {
        route("/api/user/authenticate"){
            get {
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}