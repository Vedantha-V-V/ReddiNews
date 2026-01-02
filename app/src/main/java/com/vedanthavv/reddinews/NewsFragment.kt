package com.vedanthavv.reddinews

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

        // 2) Prepare dummy data
        val dummy = listOf(
            NewsItem(
                title = "SpaceX launches new satellite",
                description = "SpaceX successfully launched a new communications satellite into orbit.",
                url = "https://example.com/spacex",
                imageUrl = "https://picsum.photos/id/237/200/200",
                author = "Jane Doe",
                comments = 120,
                publishedAt = (System.currentTimeMillis() / 1000) - 3600 // 1 hour ago
            ),
            NewsItem(
                title = "Local Startup raises Series A",
                description = "A local startup raised funds to expand its product offerings.",
                url = "https://example.com/startup",
                imageUrl = "https://picsum.photos/id/238/200/200",
                author = "John Smith",
                comments = 45,
                publishedAt = (System.currentTimeMillis() / 1000) - 7200 // 2 hours ago
            ),
            NewsItem(
                title = "City Marathon draws thousands",
                description = "Runners from across the country participated in the annual city marathon.",
                url = "https://example.com/marathon",
                imageUrl = "https://picsum.photos/id/239/200/200",
                author = "Alex Lee",
                comments = 8,
                publishedAt = (System.currentTimeMillis() / 1000) - 86400 // 1 day ago
            )
        )

        // start with dummy data, adapter is updatable
        val adapter = NewsAdapter(dummy, onItemClicked = { news: NewsItem ->
            Toast.makeText(requireContext(), "Clicked: ${news.title}", Toast.LENGTH_SHORT).show()
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
                            title = data.title,
                            description = data.title, // reddit has no separate description; use title as placeholder
                            url = data.url,
                            imageUrl = if (data.thumbnail.startsWith("http")) data.thumbnail else "",
                            author = data.author,
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