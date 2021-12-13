package com.example.routes

import com.example.plugins.userId
import com.example.util.Constants
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.content.*
import java.io.File
import java.util.*

val ApplicationCall.userId: String
    get() = principal<JWTPrincipal>()?.userId.toString()

fun PartData.FileItem.save(path: String): String{
    val fileBytes = streamProvider().readBytes()
    val fileExtension = originalFileName?.takeLastWhile { it != '.'}
    val fileName = UUID.randomUUID().toString() + "." + fileExtension
    File("$path/$fileName").writeBytes(fileBytes)
    return fileName
}