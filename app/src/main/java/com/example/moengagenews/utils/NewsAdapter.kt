package com.example.moengagenews.utils

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moengagenews.data.model.News
import com.example.moengagenews.R
import com.example.moengagenews.network.toStringFormat

class NewsAdapter(private val newsList: List<News>): RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    var mClickListener: ItemClickListener? = null

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener{
        val tvSource: TextView
        val tvDate: TextView
        val tvTitle: TextView
        val tvDescription: TextView
        val tvContent: TextView
        val imageViewNews: ImageView
        val arrowText : TextView
        var state = 0
        init {

            // Define click listener for the ViewHolder's View
            tvSource = view.findViewById(R.id.tv_source)
            tvDate = view.findViewById(R.id.tv_date)
            tvTitle = view.findViewById(R.id.tv_title)
            tvDescription = view.findViewById(R.id.tv_description)

            imageViewNews = view.findViewById(R.id.image_news)

            tvContent = view.findViewById(R.id.tv_content)

            arrowText = view.findViewById(R.id.tv_read_more)
            arrowText.setOnClickListener{
                if (state == 0){
                    state = 1
                    tvContent.visibility = View.VISIBLE
                    arrowText.text = itemView.context.resources.getString(R.string.up_symbol)
                }else{
                    state = 0
                    tvContent.visibility = View.GONE
                    arrowText.text = itemView.context.resources.getString(R.string.down_symbol)
                }
            }
            itemView.setOnClickListener(this)
        }


        override fun onClick(v: View?) {
            mClickListener?.let {
                mClickListener?.onItemClick(v, adapterPosition)
            }
        }
    }

//     allows clicks events to be caught
    fun setClickListener(itemClickListener: ItemClickListener) {
        this.mClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_article_holder, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        return newsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("onBindViewHolder", "Data set: ${newsList.size} List: $newsList")
        holder.tvSource.text = newsList[position].source?.name
        holder.tvDate.text = newsList[position].publishedAt?.toStringFormat()
        holder.tvTitle.text = newsList[position].title
        holder.tvDescription.text = newsList[position].description
        holder.tvContent.text = newsList[position].content
        Glide.with(holder.itemView.context)
            .load(newsList[position].urlToImage)
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .into(holder.imageViewNews)
    }
}


interface ItemClickListener {
    fun onItemClick(view: View?, position: Int)
}