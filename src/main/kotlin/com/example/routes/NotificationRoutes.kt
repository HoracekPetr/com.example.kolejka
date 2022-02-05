package com.example.routes

import com.example.data.responses.BasicApiResponse
import com.example.service.NotificationService
import com.example.service.PostService
import com.example.util.Constants
import com.example.util.QueryParameters
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.getNotificationsForUser(
    notificationService: NotificationService
) {
    authenticate {
        route("/api/notifications/get") {
            get {
                val page = call.parameters[QueryParameters.PARAM_PAGE]?.toIntOrNull() ?: 0
                val pageSize =
                    call.parameters[QueryParameters.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.POSTS_PAGE_SIZE

                val notificationsForUser = notificationService.getNotificationsForUser(call.userId, page, pageSize)
                call.respond(
                    HttpStatusCode.OK,
                    notificationsForUser
                )
            }
        }
    }
}

fun Route.getNotificationCount(
    notificationService: NotificationService
) {
    authenticate{
        route("/api/notifications/getCount") {
            get {
                val userId = call.userId
                println("USERID: ${call.userId}")

                val notificationCount = notificationService.getNotificationCount(userId)

                call.respond(
                    HttpStatusCode.OK,
                    notificationCount
                )
            }
        }
    }
}