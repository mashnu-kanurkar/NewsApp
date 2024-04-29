package com.example.moengagenews.utils

/***
 * Sealed class to be used while setting the sort order
 */
sealed class SortOrder{
    object Descending: SortOrder()
    object Ascending: SortOrder()
}