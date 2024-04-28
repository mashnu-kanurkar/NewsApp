package com.example.moengagenews.utils

sealed class SortOrder{
    object Descending: SortOrder()
    object Ascending: SortOrder()
}