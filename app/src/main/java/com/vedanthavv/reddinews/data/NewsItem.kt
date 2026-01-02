package com.vedanthavv.reddinews.data

// Added optional author, comments and publishedAt with sensible defaults so the UI can show placeholders.
data class NewsItem(
    val title: String,
    val subreddit: String,
    val url: String,
    val imageUrl: String,
    val comments: Int = 0,
    val publishedAt: Long = System.currentTimeMillis() / 1000 // seconds since epoch
)
