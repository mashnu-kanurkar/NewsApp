package com.example.moengagenews.network

import android.util.Log
import com.example.moengagenews.data.model.News
import com.example.moengagenews.data.model.Source
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.lang.reflect.Type

/***
 * NewsResponseParser to read the JSON string and converts it to [List<News>]
 */
class NewsResponseParser: ResponseParser<List<News>>() {
    private val TAG = this::class.simpleName
    override fun parse(response: String): List<News> {
        val reader = JSONObject(response)
        val status = reader.getString("status")
        Log.d(TAG, "response status: $status")
        val articles = reader.getJSONArray("articles")
        val newsList: MutableList<News> = mutableListOf()
        for (i in 0 until articles.length()) {
            try {
                val article: JSONObject = articles.getJSONObject(i)
                val sourceJson = article.getJSONObject("source")
                val sourceId = sourceJson.getString("id")
                val sourceName = sourceJson.getString("name")
                val source = Source(sourceId, sourceName)
                val author = article.getString("author")
                val title = article.getString("title")
                val description = article.getString("description")
                val content = article.getString("content")
                val date = article.getString("publishedAt").toDate()
                val url = article.getString("url")
                val urlToImage = article.getString("urlToImage")
                val news = News(source = source, author = author, title = title,
                    description = description, content = content,
                    publishedAt = date, url = url, urlToImage = urlToImage)
                newsList.add(news)
            }catch (e: Exception){
                Log.e(TAG, "Json parser error", e)
            }
        }
        return newsList
    }
}