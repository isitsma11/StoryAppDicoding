package com.mastercoding.mystoryappsubmissionawal.story

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mastercoding.mystoryappsubmissionawal.R
import com.mastercoding.mystoryappsubmissionawal.api.ApiService
import com.mastercoding.mystoryappsubmissionawal.model.Story
import com.mastercoding.mystoryappsubmissionawal.story.adapter.StoryListAdapter
import com.mastercoding.mystoryappsubmissionawal.story.adapter.LoadingStateAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StoryListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StoryListAdapter

    private val storyViewModel: StoryViewModel by viewModels {
        StoryViewModelFactory(ApiService.create())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_story_list)

        recyclerView = findViewById(R.id.rv_story_list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the Paging 3 Adapter
        adapter = StoryListAdapter { story ->
            // Handle item click here
            Toast.makeText(this, "Clicked on: ${story.name}", Toast.LENGTH_SHORT).show()
        }

        recyclerView.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                // Retry loading logic
                observeStories() // You can call observeStories() to retry the request
            }
        )

        observeStories()
    }

    private fun observeStories() {
        lifecycleScope.launch {
            // Collect PagingData for stories
            storyViewModel.getStories("Bearer YOUR_TOKEN_HERE").collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        lifecycleScope.launch {
            // Collect LoadState for the adapter
            adapter.addLoadStateListener { loadState ->
                // Handle LoadState changes (e.g., loading, error)
                // Handle errors here
                if (loadState.refresh is LoadState.Error) {
                    val error = (loadState.refresh as LoadState.Error).error
                    Toast.makeText(this@StoryListActivity, error.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
