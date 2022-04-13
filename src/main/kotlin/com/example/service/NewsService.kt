package com.example.service

import com.example.data.models.News
import com.example.data.repositories.news.NewsRepository
import com.example.data.requests.news.CreateNewsRequest
import com.example.util.ApiResponseMessages.DESC_TOO_LONG
import com.example.util.ApiResponseMessages.TITLE_TOO_LONG
import com.example.util.Constants
import com.example.util.validation.CreateNewsValidation

class NewsService(
    private val newsRepository: NewsRepository
) {
    suspend fun createNews(createNewsRequest: CreateNewsRequest): CreateNewsValidation {

        if (createNewsRequest.title.isBlank() || createNewsRequest.description.isBlank()){
            return CreateNewsValidation.EmptyFieldError
        }

        if(createNewsRequest.title.length > Constants.TITLE_MAX_CHARS){
            return CreateNewsValidation.TitleTooLong
        }

        return CreateNewsValidation.Success(
            request = newsRepository.createNews(
                News(
                    title = createNewsRequest.title,
                    description = createNewsRequest.description,
                    timestamp = System.currentTimeMillis()
                )
            )
        )
    }

    suspend fun deleteNews(newsId: String) = newsRepository.deleteNews(newsId)

    suspend fun getNews(page: Int, pageSize: Int) = newsRepository.getNews(page, pageSize)

    suspend fun getNewsById(newsId: String) = newsRepository.getNewsById(newsId)
}