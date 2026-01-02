package com.vedanthavv.reddinews.api

import com.vedanthavv.reddinews.RedditNewsResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RestAPI {

    val api: RedditApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.reddit.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RedditApi::class.java)
    }

    fun getNews(after: String, limit: String): Call<RedditNewsResponse> {
        return api.getTop(after, limit)
    }
}