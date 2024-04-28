package com.example.moengagenews.network

abstract class ResponseParser<T> {
    abstract fun parse(response: String):T
}