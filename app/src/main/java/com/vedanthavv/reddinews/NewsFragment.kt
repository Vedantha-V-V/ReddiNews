package com.vedanthavv.reddinews

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vedanthavv.reddinews.api.RestAPI
import com.vedanthavv.reddinews.data.NewsItem
import com.vedanthavv.reddinews.databinding.NewsFragmentBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


const val TAG = "MainActivity"

class NewsFragment : Fragment() {

    private lateinit var binding: NewsFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.news_fragment, container, false)
        binding = NewsFragmentBinding.bind(view)

        // find RecyclerView from layout (local variable)
        val newsList = view.findViewById<RecyclerView>(R.id.news_list)
        newsList.layoutManager = LinearLayoutManager(requireContext())


        val startData = listOf(
            NewsItem(
                title = "Fetching in Progress..",
                url = "",
                imageUrl = "${R.string.default_thumbnail}",
                subreddit = "r/loading",
                comments = 69,
                publishedAt = (System.currentTimeMillis() / 1000) - 3600
            )
        )

        val adapter = NewsAdapter(startData, onItemClicked = { news: NewsItem ->
            val redditIntent = Intent(Intent.ACTION_VIEW,Uri.parse("${news.url}"));
            startActivity(redditIntent)
        })
        newsList.adapter = adapter

        // Fetch real data from Retrofit and map response.data.children -> List<NewsItem>
        binding.progressBar.isVisible = true
        RestAPI.api.getTop("", "10").enqueue(object : Callback<RedditNewsResponse> {
            override fun onResponse(call: Call<RedditNewsResponse>, response: Response<RedditNewsResponse>) {
                binding.progressBar.isVisible = false
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val children = body.data.children
                    // Map each child to our NewsItem model
                    val items = children.map { child ->
                        val data = child.data
                        NewsItem(
                            title = if(data.title.length > 50) data.title.substring(0, 50) + "..." else data.title,
                            url = data.url,
                            imageUrl = if (data.thumbnail.startsWith("http")) data.thumbnail else "${R.string.default_thumbnail}",
                            subreddit = "r/${data.subreddit}",
                            comments = data.num_comments,
                            publishedAt = data.created
                        )
                    }
                    adapter.updateItems(items)
                } else {
                    Log.e(TAG, "Response not successful: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<RedditNewsResponse>, t: Throwable) {
                binding.progressBar.isVisible = false
                Log.e(TAG, "Network call failed", t)
            }
        })

        return view
    }
}