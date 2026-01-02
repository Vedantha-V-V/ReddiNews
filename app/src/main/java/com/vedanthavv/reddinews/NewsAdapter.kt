package com.vedanthavv.reddinews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vedanthavv.reddinews.data.NewsItem
import com.squareup.picasso.Picasso

class NewsAdapter(private var items: List<NewsItem>,
                  private val onItemClicked: (NewsItem) -> Unit) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDescription: TextView = itemView.findViewById(R.id.description)
        val tvAuthor: TextView = itemView.findViewById(R.id.author)
        val tvComments: TextView = itemView.findViewById(R.id.comments)
        val tvTime: TextView = itemView.findViewById(R.id.time)
        val imgThumbnail: ImageView = itemView.findViewById(R.id.img_thumbnail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = items[position]
        holder.tvDescription.text = news.title
        holder.tvAuthor.text = news.author
        // Use a translatable string resource for comment count
        holder.tvComments.text = holder.itemView.context.getString(R.string.comments_count, news.comments)
        holder.tvTime.text = news.publishedAt.getFriendlyTime()
        Picasso.with(holder.imgThumbnail.context)
            .load(news.imageUrl)
            .fit()
            .centerCrop()
            .placeholder(android.R.color.darker_gray)
            .error(android.R.color.darker_gray)
            .into(holder.imgThumbnail)

        // handle click
        holder.itemView.setOnClickListener {
            onItemClicked(news)
        }
    }

    // Allow updating the list after network responses
    fun updateItems(newItems: List<NewsItem>) {
        items = newItems
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int = items.size
}
