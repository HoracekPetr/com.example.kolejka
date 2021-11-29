package com.example.data.repositories.post

import com.example.data.models.Post
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.insertOne
import org.litote.kmongo.eq

class PostRepositoryImpl(
    db: CoroutineDatabase
):PostRepository {

    private val posts = db.getCollection<Post>()


    override suspend fun createPost(post: Post) {
        posts.insertOne(post)
    }


    override suspend fun getPostById(id: String): Post? {
        return posts.findOneById(id)
    }

    override suspend fun getPostsByAll(page: Int, pageSize: Int): List<Post> {
        return posts.find().skip(page*pageSize).limit(pageSize).toList()
    }

    override suspend fun getPostsByMembers(userId: String): List<Post> {
        return posts.find(Post::members contains userId).toList()
    }

    override suspend fun getPostsByCreator(userId: String): List<Post> {
        return posts.find(Post::userId eq userId).toList()
    }
}