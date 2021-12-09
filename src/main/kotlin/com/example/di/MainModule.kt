package com.example.di

import com.example.data.repositories.comment.CommentRepository
import com.example.data.repositories.comment.CommentRepositoryImpl
import com.example.data.repositories.notification.NotificationRepository
import com.example.data.repositories.notification.NotificationRepositoryImpl
import com.example.data.repositories.post.PostRepository
import com.example.data.repositories.post.PostRepositoryImpl
import com.example.data.repositories.user.UserRepository
import com.example.data.repositories.user.UserRepositoryImpl
import com.example.service.CommentService
import com.example.service.NotificationService
import com.example.service.PostService
import com.example.service.UserService
import com.example.util.Constants.DATABASE_NAME
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo


val mainModule = module {
    single {
        val client = KMongo.createClient().coroutine
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

    single {
        UserService(get())
    }

    single{
        PostService(get())
    }

    single{
        CommentService(get())
    }

    single {
        NotificationService(get(), get(), get())
    }
}