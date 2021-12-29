package com.example.plugins

import com.example.routes.*
import com.example.service.CommentService
import com.example.service.NotificationService
import com.example.service.PostService
import com.example.service.UserService
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.http.content.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    //val userRepository: UserRepository by inject()
    //val postRepository: PostRepository by inject()

    val userService: UserService by inject()
    val postService: PostService by inject()
    val commentService: CommentService by inject()
    val notificationService: NotificationService by inject()

    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()

    routing {

        //User Routes
        registerUser(userService)
        loginUser(
            userService = userService,
            jwtIssuer = jwtIssuer,
            jwtAudience = jwtAudience,
            jwtSecret = jwtSecret
        )
        getUserProfile(userService)
        updateUserProfile(userService)

        //Post Routes
        createPost(postService)
        getPostsByAll(postService)
        getPostsByCreator(postService)
        getPostsWhereUserIsMember(postService)
        deletePost(postService, commentService)
        addPostMember(postService, notificationService)

        //Comment Routes
        createComment(commentService, notificationService, postService)
        getCommentsForPost(commentService)
        deleteComment(commentService)

        //Notification Routes
        getNotificationsForUser(notificationService)

        //Static Routes
        static {
            resources("static")
        }

    }
}
