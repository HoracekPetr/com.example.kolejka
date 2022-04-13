package com.example.routes

import com.example.data.models.News
import com.example.data.requests.news.CreateNewsRequest
import com.example.data.responses.BasicApiResponse
import com.example.data.util.AccessRights
import com.example.service.NewsService
import com.example.service.UserService
import com.example.util.ApiResponseMessages
import com.example.util.ApiResponseMessages.NEWS_NOT_FOUND
import com.example.util.ApiResponseMessages.USER_NOT_FOUND
import com.example.util.Constants
import com.example.util.QueryParameters
import com.example.util.validation.CreateNewsValidation
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.createNews(
    userService: UserService,
    newsService: NewsService
) {
    authenticate {
        route("/api/news/create"){
            post {
                val user = userService.getUserById(call.userId)

                val request = call.receiveOrNull<CreateNewsRequest>() ?: kotlin.run {
                    call.respond(
                        HttpStatusCode.BadRequest
                    )
                    return@post
                }

                if(user == null){
                    call.respond(
                        HttpStatusCode.NotFound,
                        BasicApiResponse<Unit>(successful = false, message = USER_NOT_FOUND)
                    )
                    return@post
                }

                if(user.accessRights != AccessRights.Admin.type){
                    call.respond(
                        HttpStatusCode.Unauthorized
                    )
                    return@post
                }

                when(newsService.createNews(request)){
                    is CreateNewsValidation.EmptyFieldError -> {
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse<Unit>(message = ApiResponseMessages.FIELDS_BLANK, successful = false)
                        )
                    }

                    is CreateNewsValidation.TitleTooLong -> {
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse<Unit>(message = ApiResponseMessages.TITLE_TOO_LONG, successful = false)
                        )
                    }

                    is CreateNewsValidation.Success -> {
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse<Unit>(successful = true)
                        )
                    }
                }
            }
        }
    }
}

fun Route.deleteNews(
    userService: UserService,
    newsService: NewsService
) {
    authenticate{
        route("/api/news/delete/{id}"){
            delete {

                val user = userService.getUserById(call.userId)

                val newsId = call.parameters[QueryParameters.ID] ?: kotlin.run {
                    call.respond(
                        HttpStatusCode.BadRequest
                    )
                    return@delete
                }

                if(user == null){
                    call.respond(
                        HttpStatusCode.NotFound,
                        BasicApiResponse<Unit>(successful = false, message = USER_NOT_FOUND)
                    )
                    return@delete
                }

                if(user.accessRights != AccessRights.Admin.type){
                    call.respond(
                        HttpStatusCode.Unauthorized
                    )
                    return@delete
                }

                when(newsService.deleteNews(newsId)){
                    true -> {
                        call.respond(HttpStatusCode.OK, BasicApiResponse<Unit>(successful = true))
                    }
                    false -> {
                        call.respond(HttpStatusCode.BadRequest, BasicApiResponse<Unit>(successful = false))
                    }
                }

            }
        }
    }
}

fun Route.getNews(
    newsService: NewsService
) {
    authenticate {
        route("/api/news/get"){
            get {
                val page = call.parameters[QueryParameters.PARAM_PAGE]?.toIntOrNull() ?: 0
                val pageSize =
                    call.parameters[QueryParameters.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.POSTS_PAGE_SIZE

                val news = newsService.getNews(page, pageSize)

                if(news.isEmpty()){
                    call.respond(
                        OK, BasicApiResponse<Unit>(successful = false, message = ApiResponseMessages.NEWS_NOT_FOUND)
                    )
                    return@get
                }

                call.respond(
                    OK, BasicApiResponse(successful = true, data = news)
                )
            }
        }
    }
}

fun Route.getNewsById(
    newsService: NewsService
){
    authenticate{
        route("/api/news/get/{id}"){
            get {
                val newsId = call.parameters[QueryParameters.ID] ?: kotlin.run {
                    call.respond(
                        HttpStatusCode.BadRequest
                    )
                    return@get
                }

                val news = newsService.getNewsById(newsId)

                if(news == null){
                    call.respond(OK, BasicApiResponse<Unit>(successful = false, message = NEWS_NOT_FOUND))
                    return@get
                }

                news.let {
                    call.respond(OK, BasicApiResponse(successful = true, data = it))
                }
            }
        }
    }
}