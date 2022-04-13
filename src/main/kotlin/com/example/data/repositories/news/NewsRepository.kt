package com.example.data.repositories.news

import com.example.data.models.News
import com.example.util.Constants

interface NewsRepository {

    suspend fun createNews(news: News): Boolean

    suspend fun deleteNews(newsId: String): Boolean

    suspend fun getNews(
        page: Int = 0,
        pageSize: Int = Constants.POSTS_PAGE_SIZE
    ): List<News>

}