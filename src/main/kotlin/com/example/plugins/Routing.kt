package com.example.plugins

import com.example.data.repositories.user.UserRepository
import com.example.routes.createUserRoute
import com.example.routes.loginUserRoute
import io.ktor.routing.*
import io.ktor.application.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    val userRepository: UserRepository by inject()

    routing {
        createUserRoute(userRepository)
        loginUserRoute(userRepository)
    }
}
