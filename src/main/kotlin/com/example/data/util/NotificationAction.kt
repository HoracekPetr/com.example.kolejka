package com.example.data.util

sealed class NotificationAction(val type: Int) {
    object JoinedEvent : NotificationAction(0)
    object CalledDibs : NotificationAction(1)
    object CommentedOn: NotificationAction(2)
}
