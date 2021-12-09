package com.example.data.util

import com.example.data.models.Post

sealed class PostType(val type: Int) {
    object Event : PostType(0)
    object Offer : PostType(1)

    companion object{
        fun fromType(type: Int): PostType{
            return when(type){
                0 -> Event
                1 -> Offer
                else -> Event
            }
        }
    }
}
