package com.example.cronifi

data class Reminder(
    val message:String,
    val sender:String,
    val receiver:String,
    val time:String
) {
}