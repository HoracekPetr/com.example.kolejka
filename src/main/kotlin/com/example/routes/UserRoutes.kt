package com.example.routes

import com.example.data.repositories.user.UserRepository
import com.example.data.models.User
import com.example.data.requests.CreateAccountRequest
import com.example.data.requests.LoginRequest
import com.example.data.responses.BasicApiResponse
import com.example.util.ApiResponseMessages.FIELDS_BLANK
import com.example.util.ApiResponseMessages.INVALID_CREDENTIALS
import com.example.util.ApiResponseMessages.USER_ALREADY_EXISTS
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import org.koin.ktor.ext.inject

fun Route.createUserRoute(
    userRepository: UserRepository
) {

    route("/api/user/create") {
        post {
            val request = call.receiveOrNull<CreateAccountRequest>() ?: kotlin.run {
                call.respond(BadRequest)
                return@post
            }

            val userExist = userRepository.getUserByEmail(request.email) != null

            if (userExist) {
                call.respond(
                    BasicApiResponse(USER_ALREADY_EXISTS, false)
                )
                return@post
            }
            if (request.email.isBlank() || request.password.isBlank() || request.username.isBlank()) {
                call.respond(
                    BasicApiResponse(FIELDS_BLANK, false)
                )
            }

            userRepository.createUser(
                User(
                    email = request.email,
                    username = request.username,
                    password = request.password,
                    profileImageURL = ""
                )
            )
            call.respond(
                OK,
                BasicApiResponse(successful = true)
            )
        }
    }
}

fun Route.loginUserRoute(userRepository: UserRepository) {

    route("/api/user/login") {
        post {
            val request = call.receiveOrNull<LoginRequest>() ?: kotlin.run {
                call.respond(BadRequest)
                return@post
            }

            if (request.email.isBlank() || request.password.isBlank()) {
                call.respond(BadRequest)
                return@post
            }


            val isCorrectPassword = userRepository.doesPasswordForUserMatch(request.email, request.password)

            if(isCorrectPassword){
                call.respond(
                    OK,
                    BasicApiResponse(successful = true)
                )
            } else
            {
                call.respond(
                    OK,
                    BasicApiResponse(message = INVALID_CREDENTIALS, successful = false)
                )
            }
        }
    }
}