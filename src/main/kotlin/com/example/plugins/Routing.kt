package com.example.plugins

import com.example.data.models.push_notification.PushNotification
import com.example.data.models.push_notification.PushNotificationMessage
import com.example.data.repositories.push_notifications.PushNotificationRepository
import com.example.data.repositories.push_notifications.PushNotificationRepositoryImpl
import com.example.data.util.OneSignalObjects
import com.example.routes.*
import com.example.service.*
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject

fun Application.configureRouting(client: HttpClient) {

    //val userRepository: UserRepository by inject()
    //val postRepository: PostRepository by inject()

    val userService: UserService by inject()
    val postService: PostService by inject()
    val commentService: CommentService by inject()
    val notificationService: NotificationService by inject()
    val newsService: NewsService by inject()
    val pushNotificationService: PushNotificationService by inject()

    val apiKey = environment.config.property("onesignal.api_key").getString()

    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()

    routing {

        //Auth Routes
        registerUser(userService, notificationService)
        loginUser(
            userService = userService,
            jwtIssuer = jwtIssuer,
            jwtAudience = jwtAudience,
            jwtSecret = jwtSecret
        )
        authenticate()

        //User Routes
        getUserProfile(userService)
        getOtherUserProfile(userService)
        updateUserInfo(userService, postService, commentService, notificationService)
        changeUserPassword(userService)
        getUserId(userService)

        //Push
        sendPushNotification(pushNotificationService, apiKey, client)

        //Post Routes
        createNewPost(postService, userService)

        getPostById(postService)
        getPostsByAll(postService)
        getPostsByCreator(postService)
        getPostsWhereUserIsMember(postService)
        getPostsByOtherCreator(postService)
        deletePost(postService, commentService, notificationService)
        addPostMember(postService, notificationService, commentService)
        editPostInfo(postService)

        //Comment Routes
        createComment(commentService, notificationService, postService)
        getCommentsForPost(commentService)
        deleteComment(commentService)

        //Notification Routes
        getNotificationsForUser(notificationService)
        getNotificationCount(notificationService)
        setNotificationsToZero(notificationService)
        deleteNotificationsForUser(notificationService)

        //News Routes
        createNews(userService, newsService)
        deleteNews(userService, newsService)
        getNews(newsService)
        getNewsById(newsService)


        //Static Routes
        static {
            resources("static")
        }

    }
}
