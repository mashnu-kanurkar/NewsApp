package com.example.moengagenews.network

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

fun String.toDate(): Date? {
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    format.timeZone = TimeZone.getTimeZone("UTC")
    return format.parse(this)
}

fun Date.toStringFormat():String{
    val format = SimpleDateFormat("dd MMMM, yyyy hh:mm a", Locale.US)
    return format.format(this)
}