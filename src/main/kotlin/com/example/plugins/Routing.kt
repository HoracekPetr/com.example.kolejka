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
import java.util.*

fun Application.configureRouting() {

    //val userRepository: UserRepository by inject()
    //val postRepository: PostRepository by inject()

    val userService: UserService by inject()
    val postService: PostService by inject()

    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()

    routing {
        //User Routes
        createUserRoute(userService)
        loginUserRoute(
            userService = userService,
            jwtIssuer = jwtIssuer,
            jwtAudience = jwtAudience,
            jwtSecret = jwtSecret
        )

        //Post Routes
        createPostRoute(postService, userService)
    }
}
