package com.example.data.repositories.push_notifications

import com.example.data.models.push_notification.PushNotification
import com.example.data.util.OneSignalObjects
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.features.*
import io.ktor.http.*

class PushNotificationRepositoryImpl: PushNotificationRepository {

    override suspend fun sendPushNotification(pushNotification: PushNotification, client: HttpClient, apiKey: String): Boolean {

        return try {
            client.post<String>{
                url(OneSignalObjects.NOTIFICATIONS_URL)
                contentType(ContentType.Application.Json)
                header("Authorization", "Basic $apiKey")
                body = pushNotification
            }

            client.close()

            true

        } catch (e: Exception){
            e.printStackTrace()
            false
        }

    }
}