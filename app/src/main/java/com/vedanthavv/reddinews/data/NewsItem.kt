package com.vedanthavv.reddinews.data

data class NewsItem(
    val title: String,
    val subreddit: String,
    val url: String,
    val imageUrl: String,
    val comments: Int = 0,
    val publishedAt: Long = System.currentTimeMillis() / 1000
)
