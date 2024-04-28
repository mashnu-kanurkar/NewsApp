package com.example.moengagenews.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moengagenews.R
import com.example.moengagenews.data.model.News
import com.example.moengagenews.network.NetworkMonitor
import com.example.moengagenews.network.NetworkState
import com.example.moengagenews.utils.ItemClickListener
import com.example.moengagenews.utils.NewsAdapter
import com.example.moengagenews.utils.SortOrder
import com.example.moengagenews.utils.TagClickListener
import com.example.moengagenews.viewmodel.MainActivityViewModel


class MainActivity : AppCompatActivity(), ItemClickListener, TagClickListener {
    private val TAG = this::class.simpleName
    private lateinit var viewmodel: MainActivityViewModel
    private var newsList: List<News> = emptyList()
    private var tagList: List<String> = mutableListOf("All")
    private lateinit var sortImage: ImageView
    private var isMonitoringNetwork = false

    fun getConnectivityManager() = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sortImage = findViewById(R.id.image_sort)
        sortImage.setOnClickListener {
            showSortOptionAlert()
        }

        val recyclerViewNews: RecyclerView = findViewById(R.id.recycler_view_news)
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerViewNews.layoutManager = llm


//        val recyclerViewTags: RecyclerView = findViewById(R.id.recycler_view_tag)
//        recyclerViewTags.layoutManager = LinearLayoutManager(this)

        viewmodel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        viewmodel.newsArticles.observe(this) {
            Log.d(TAG, "Received list: $it")
            val adapter = NewsAdapter(it)
            recyclerViewNews.adapter = adapter
            adapter.setClickListener(this)
            adapter.notifyDataSetChanged()
            newsList = it
        }

        viewmodel.tagLiveData.observe(this){
            Log.d(TAG, "Tags list: $it")
//            val tagAdapter = TagsAdapter(it)
            //recyclerViewTags.adapter = tagAdapter
//            tagAdapter.setClickListener(this)
        }
        NetworkMonitor.networkState.observe(this){
            when(it){
                is NetworkState.OnAvailable ->{
                    showNetworkOnUi()
                    if (newsList.isEmpty()){
                        viewmodel.fetchNewsArticles()
                    }
                }
                is NetworkState.OnLost ->{
                    showNetworkOffUi()
                }
            }
        }

    }

    private fun showNetworkOffUi(){
        setContentView(R.layout.network_lost_ui)
    }
    private fun showNetworkOnUi(){
        setContentView(R.layout.activity_main)
    }

    override fun onItemClick(view: View?, position: Int) {
        Log.d(TAG, "Item clicked")
        try {
            val url = newsList.get(position).url
            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse(url))
            startActivity(i)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isMonitoringNetwork.not()){
            getConnectivityManager().registerNetworkCallback(NetworkMonitor.getNetworkRequest(), NetworkMonitor.getNetworkCallBack())
            isMonitoringNetwork = true
        }
    }

    override fun onPause() {
        super.onPause()
        if (isMonitoringNetwork){
            getConnectivityManager().unregisterNetworkCallback(NetworkMonitor.getNetworkCallBack())
            isMonitoringNetwork = true
        }

    }

    override fun onTagClick(view: View?, position: Int) {
        Log.d(TAG, "Tag clicked")
        try {
            val tag = tagList.get(position)
            viewmodel.filterByTag(tag)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun showSortOptionAlert(){
        val builderSingle = AlertDialog.Builder(this@MainActivity)
        builderSingle.setTitle("Sort By")

        val arrayAdapter =
            ArrayAdapter<String>(this@MainActivity, android.R.layout.select_dialog_item)
        arrayAdapter.add("Date: Descending")
        arrayAdapter.add("Date: Ascending")

        builderSingle.setNegativeButton("cancel") { dialog, which ->
            dialog.dismiss() }

        builderSingle.setAdapter(arrayAdapter) { dialog, which ->
            val strName = arrayAdapter.getItem(which)
            if (strName.equals("Date: Descending")){
                viewmodel.sortNewsListByDate(SortOrder.Descending)
            }else{
                viewmodel.sortNewsListByDate(SortOrder.Ascending)
            }
            dialog.dismiss()
        }
        builderSingle.show()
    }
}