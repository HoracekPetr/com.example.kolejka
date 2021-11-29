package com.example.plugins

import com.example.data.repositories.post.PostRepository
import com.example.data.repositories.user.UserRepository
import com.example.routes.createPostRoute
import com.example.routes.createUserRoute
import com.example.routes.loginUserRoute
import com.example.service.PostService
import com.example.service.UserService
import io.ktor.routing.*
import io.ktor.application.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    //val userRepository: UserRepository by inject()
    //val postRepository: PostRepository by inject()

    val userService: UserService by inject()
    val postService: PostService by inject()

    routing {
        //User Routes
        createUserRoute(userService)
        loginUserRoute(userService)

        //Post Routes
        createPostRoute(postService)
    }
}
