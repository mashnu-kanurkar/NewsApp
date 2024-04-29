package com.example.moengagenews.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.net.URL
import javax.net.ssl.HttpsURLConnection


/***
 * Network manager class is a generic class performs remote data fetch action and process it with applicable response parser
 * [responseParser] ResponseParser object e.g NewsResponseParser
 */
class NetworkManager<T> (private var responseParser: ResponseParser<T>?){
    private val TAG = this::class.java.simpleName
    fun setResponseParser(responseParser: ResponseParser<T>){
        this.responseParser = responseParser
    }

    /***
     * Creates a HTTP connection and process the response.
     * @param [String] endpoint from where the data needs to be fetched
     * @return [Result] either [Result.Success] or [Result.Error]
     */
    suspend fun getRemoteResponse(url: String): Result<T> {
        if (url.contains("http").not()) {
            Log.e(TAG, "invalid url -> $url")
            return Result.Error(IllegalArgumentException("Invalid URL"))
        }
        Log.d(TAG, "fetching remote data from $url")
        return withContext(Dispatchers.IO) {
        var conn: HttpsURLConnection? = null
        try {
            conn = buildHttpsURLConnection(url)
            val responseCode = conn.responseCode
            if (responseCode != 200) {
                Log.e(TAG, "failed to get remote data, http status code $responseCode")
                return@withContext Result.Error(IOException("HTTP error code: $responseCode"))
            }
            val inputStream = conn.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            reader.close()
            if (responseParser == null) {
                return@withContext Result.Success(stringBuilder.toString() as T)
            } else {
                val res = responseParser?.parse(stringBuilder.toString())
                return@withContext Result.Success(res as T)
            }
        } catch (e: Exception) {
            Log.e(TAG, "failed to get remote data", e)
            return@withContext Result.Error(e)
        } finally {
            if (conn != null) {
                try {
                    conn.inputStream.close()
                    conn.disconnect()
                } catch (t: Throwable) {
                    // Do nothing
                }
            }
        }
    }
    }
    private fun buildHttpsURLConnection(urlString: String?): HttpsURLConnection {
        val url = URL(urlString)
        val conn = url.openConnection() as HttpsURLConnection
        conn.connectTimeout = 10000
        conn.readTimeout = 10000
        return conn
    }
}