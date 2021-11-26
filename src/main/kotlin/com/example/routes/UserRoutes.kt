package com.example.routes

import com.example.controller.user.UserController
import com.example.data.models.User
import com.example.data.requests.CreateAccountRequest
import com.example.data.responses.BasicApiResponse
import com.example.util.ApiResponseMessages.FIELDS_BLANK
import com.example.util.ApiResponseMessages.USER_ALREADY_EXISTS
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import org.koin.ktor.ext.inject

fun Route.userRoutes(

) {
    val userController: UserController by inject()

    route("/api/user/create") {
        post {
            val request = call.receiveOrNull<CreateAccountRequest>() ?: kotlin.run {
                call.respond(BadRequest)
                return@post
            }

            val userExist = userController.getUserByEmail(request.email) != null

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

            userController.createUser(
                User(
                    email = request.email,
                    username = request.username,
                    password = request.password,
                    profileImageURL = ""
                )
            )
            call.respond(
                BasicApiResponse(successful = true)
            )
        }
    }
}