package com.example.moengagenews.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moengagenews.R
import com.example.moengagenews.data.model.News
import com.example.moengagenews.network.LoadingState
import com.example.moengagenews.network.NetworkMonitor
import com.example.moengagenews.utils.ItemClickListener
import com.example.moengagenews.utils.NewsAdapter
import com.example.moengagenews.utils.SortOrder
import com.example.moengagenews.utils.TagClickListener
import com.example.moengagenews.utils.TagsAdapter
import com.example.moengagenews.viewmodel.MainActivityViewModel


class MainActivity : AppCompatActivity(), ItemClickListener, TagClickListener {
    private val TAG = this::class.simpleName
    private lateinit var viewmodel: MainActivityViewModel
    private var newsList: List<News> = emptyList()
    private var tagList: MutableList<String> = mutableListOf("All")
    private lateinit var sortImage: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewmodel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        var viewGroup: Group = findViewById(R.id.group_main_activity)
        val progressBar = findViewById<ProgressBar>(R.id.progress_circular)
        viewmodel.loadingState.observe(this){
            when(it){
                is LoadingState.IsLoading ->{
                    Log.d(TAG, "is loading: ${it.isLoading} ")
                    if (it.isLoading){
                        progressBar.visibility = View.VISIBLE
                        viewGroup.visibility = View.GONE
                    }else{
                        progressBar.visibility = View.GONE
                        viewGroup.visibility = View.VISIBLE
                    }
                }
                is LoadingState.hasError ->{
                    progressBar.visibility = View.GONE
                    viewGroup.visibility = View.VISIBLE
                    startActivity(Intent(this@MainActivity, NoNetworkActivity::class.java))
                }
            }
        }
        sortImage = findViewById(R.id.image_sort)
        sortImage.setOnClickListener {
            showSortOptionAlert()
        }

        val recyclerViewNews: RecyclerView = findViewById(R.id.recycler_view_news)
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerViewNews.layoutManager = llm


        val recyclerViewTags: RecyclerView = findViewById(R.id.recycler_view_tag)
        recyclerViewTags.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        viewmodel.newsArticles.observe(this) {
            try {
                Log.d(TAG, "Received list: $it")
                val adapter = NewsAdapter(it)
                recyclerViewNews.adapter = adapter
                adapter.setClickListener(this)
                adapter.notifyDataSetChanged()
                newsList = it
            }catch (e: Exception){
                e.printStackTrace()
            }

        }

        viewmodel.tagLiveData.observe(this){
            try {
                Log.d(TAG, "Tags set: $it")
                tagList.addAll(it)
                val tagAdapter = TagsAdapter(tagList)
                recyclerViewTags.adapter = tagAdapter
                tagAdapter.notifyDataSetChanged()
                tagAdapter.setClickListener(this)
            }catch (e: Exception){
                e.printStackTrace()
            }

        }
        NetworkMonitor.networkState.observe(this, activeNetworkStateObserver)

    }

    private fun changeUIForNetworkChange(isConnected: Boolean){
        if (isConnected){
            if (newsList.isEmpty()){
                viewmodel.fetchNewsArticles()
            }
        }else{
            if (newsList.isEmpty()){
                startActivity(Intent(this@MainActivity, NoNetworkActivity::class.java))
            }
        }
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

    /**
     * Observer for internet connectivity status live-data
     */
    private val activeNetworkStateObserver: Observer<Boolean> =
        Observer<Boolean> { value ->
            Log.d(TAG, "Network changed: $value")
            changeUIForNetworkChange(value)
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