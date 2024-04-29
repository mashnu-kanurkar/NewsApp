package com.example.moengagenews.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moengagenews.data.model.News
import com.example.moengagenews.network.LoadingState
import kotlinx.coroutines.launch
import com.example.moengagenews.network.Result
import com.example.moengagenews.network.NetworkManager
import com.example.moengagenews.network.NewsResponseParser
import com.example.moengagenews.utils.SortOrder

class MainActivityViewModel: ViewModel() {
    private val newsURL = "https://candidate-test-data-moengage.s3.amazonaws.com/Android/news-api-feed/staticResponse.json"
    private var sortOrder: SortOrder = SortOrder.Descending
    private var currentFilter = "All"
    private var newsList : List<News> = mutableListOf()
    val tagLiveData: MutableLiveData<Set<String>> by lazy {
        MutableLiveData()
    }

    val newsArticles: MutableLiveData<List<News>> by lazy {
        MutableLiveData<List<News>>()
    }

    val loadingState: MutableLiveData<LoadingState> by lazy {
        MutableLiveData<LoadingState>()
    }

    init {
        fetchNewsArticles()
    }

    fun fetchNewsArticles(){
        viewModelScope.launch {
            loadingState.postValue(LoadingState.IsLoading(true))
            val networkManager = NetworkManager(NewsResponseParser())
            val result = networkManager.getRemoteResponse(newsURL)
            Log.d("MainActivityViewModel", "Article fetch completed")
            when(result){
                is Result.Success -> {
                    Log.d("MainActivityViewModel", "size: ${result.data.size}")
                    newsList = result.data
                    newsArticles.postValue(newsList)
                    loadingState.postValue(LoadingState.IsLoading(false))
                    getTagsFromList(newsList)
                    Log.d("MainActivityViewModel",newsList.toString() )
                }
                is Result.Error ->{
                    //show some error related functionality
                    loadingState.postValue(LoadingState.hasError(result.exception.toString()))
                    Log.d("MainActivityViewModel",result.exception.toString() )
                }

                else -> {}
            }
        }
    }

    fun getTagsFromList(newsList: List<News>){
        viewModelScope.launch {
            val newsWithTagList = newsList.filter {
                it.source?.name != null
            }
            val tagsSet = mutableSetOf<String>()
            newsWithTagList.forEach {
                it.source?.name?.let {
                    tagsSet.add(it)
                }
            }
            tagLiveData.postValue(tagsSet)
        }

    }

    fun sortNewsListByDate(order: SortOrder){
        viewModelScope.launch {
            sortOrder = order
            when(order){
                SortOrder.Ascending ->{
                    val sorted = newsArticles.value?.sortedByDescending { it.publishedAt }
                    newsArticles.postValue(sorted)
                    newsList.sortedByDescending { it.publishedAt }
                }
                SortOrder.Descending ->{
                    val sorted = newsArticles.value?.sortedBy { it.publishedAt }
                    newsArticles.postValue(sorted)
                    newsList.sortedBy { it.publishedAt }
                }

                else -> {}
            }
        }

    }

    fun filterByTag(tag: String){
        viewModelScope.launch {
            currentFilter = tag
            if (tag.equals("All", ignoreCase = true)){
                newsArticles.postValue(newsList)
            }else{
                val filtered = newsList.filter {
                    it.source?.name.equals(tag, ignoreCase = true)
                }
                newsArticles.postValue(filtered)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

    }

}