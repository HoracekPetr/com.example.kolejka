package com.example.di

import com.example.data.repositories.comment.CommentRepository
import com.example.data.repositories.comment.CommentRepositoryImpl
import com.example.data.repositories.news.NewsRepository
import com.example.data.repositories.news.NewsRepositoryImpl
import com.example.data.repositories.notification.NotificationRepository
import com.example.data.repositories.notification.NotificationRepositoryImpl
import com.example.data.repositories.post.PostRepository
import com.example.data.repositories.post.PostRepositoryImpl
import com.example.data.repositories.user.UserRepository
import com.example.data.repositories.user.UserRepositoryImpl
import com.example.service.*
import com.example.util.Constants.DATABASE_NAME
import com.google.gson.Gson
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo


val mainModule = module {
    single {
        val client = KMongo.createClient(System.getenv("MONGO_URI")).coroutine
        client.getDatabase(DATABASE_NAME)
    }

    single<UserRepository> {
        UserRepositoryImpl(get())
    }

    single<PostRepository> {
        PostRepositoryImpl(get())
    }

    single<CommentRepository>{
        CommentRepositoryImpl(get())
    }

    single <NotificationRepository>{
        NotificationRepositoryImpl(get())
    }

    single <NewsRepository>{
        NewsRepositoryImpl(get())
    }

    single {
        UserService(get())
    }

    single{
        PostService(get())
    }

    single{
        CommentService(get(), get())
    }

    single {
        NotificationService(get(), get(), get())
    }

    single {
        NewsService(get())
    }

    single{
        Gson()
    }
}