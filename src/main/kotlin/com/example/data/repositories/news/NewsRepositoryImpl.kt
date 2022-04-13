package com.example.data.repositories.news

import com.example.data.models.News
import org.litote.kmongo.coroutine.CoroutineDatabase

class NewsRepositoryImpl(
    db: CoroutineDatabase
): NewsRepository {

    private val newsCollection = db.getCollection<News>()

    override suspend fun createNews(news: News): Boolean {
        return newsCollection.insertOne(news).wasAcknowledged()
    }

    override suspend fun deleteNews(newsId: String): Boolean {
        return newsCollection.deleteOneById(newsId).wasAcknowledged()
    }

    override suspend fun getNews(page: Int, pageSize: Int): List<News> {
        return newsCollection
            .find()
            .skip(page * pageSize)
            .limit(pageSize)
            .descendingSort(News::timestamp)
            .toList()
    }

    override suspend fun getNewsById(newsId: String): News? {
        return newsCollection.findOneById(newsId)
    }
}