package com.example

import com.example.di.mainModule
import io.ktor.application.*
import com.example.plugins.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.server.netty.*
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.modules
import org.koin.logger.SLF4JLogger

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureSecurity()
    val client = HttpClient(CIO)
    install(Koin){
        modules(mainModule)
    }
    configureRouting(client)
    configureSerialization()
    configureMonitoring()
    configureHTTP()
}
