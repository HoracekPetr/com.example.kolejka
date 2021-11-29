package com.example

import com.example.di.mainModule
import io.ktor.application.*
import com.example.plugins.*
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.modules
import org.koin.logger.SLF4JLogger

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureSecurity()
    install(Koin){
        modules(mainModule)
    }
    configureRouting()
    configureSerialization()
    configureMonitoring()
    configureHTTP()
}
