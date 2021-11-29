package com.example.di

import com.example.data.repositories.post.PostRepository
import com.example.data.repositories.post.PostRepositoryImpl
import com.example.data.repositories.user.UserRepository
import com.example.data.repositories.user.UserRepositoryImpl
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

    single {
        UserService(get())
    }

    single{
        PostService(get())
    }
}