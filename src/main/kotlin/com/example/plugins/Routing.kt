package com.example.plugins

import com.example.routes.*
import com.example.service.CommentService
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
    val commentService: CommentService by inject()

    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()

    routing {

        //User Routes
        createUser(userService)
        loginUser(
            userService = userService,
            jwtIssuer = jwtIssuer,
            jwtAudience = jwtAudience,
            jwtSecret = jwtSecret
        )

        //Post Routes
        createPost(postService, userService)
        getPostsByAll(postService)
        getPostsByCreator(postService, userService)
        getPostsWhereUserIsMember(postService, userService)
        deletePost(postService, userService)
        addPostMember(postService, userService)

        //Comment Routes
        createComment(commentService, userService)
        getCommentsForPost(commentService)
        deleteComment(commentService,userService)

    }
}
