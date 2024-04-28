package com.example.moengagenews.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moengagenews.R

class TagsAdapter(private val tagsList: List<String>): RecyclerView.Adapter<TagsAdapter.ViewHolder>() {
    var mClickListener: TagClickListener? = null

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener{
        val tvTag: TextView

        init {
            // Define click listener for the ViewHolder's View
            tvTag = view.findViewById(R.id.tv_tag)
            tvTag.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            mClickListener?.let {
                mClickListener?.onTagClick(v, adapterPosition)
            }
        }
    }
    // allows clicks events to be caught
    fun setClickListener(tagClickListener: TagClickListener) {
        this.mClickListener = tagClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_tag_holder, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        return tagsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvTag.text = tagsList[position]
    }
}


interface TagClickListener {
    fun onTagClick(view: View?, position: Int)
}