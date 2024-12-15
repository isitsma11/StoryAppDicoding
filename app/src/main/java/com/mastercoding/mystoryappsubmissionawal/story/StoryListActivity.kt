package com.mastercoding.mystoryappsubmissionawal.story

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mastercoding.mystoryappsubmissionawal.R
import com.mastercoding.mystoryappsubmissionawal.api.ApiService
import com.mastercoding.mystoryappsubmissionawal.auth.LoginActivity
import com.mastercoding.mystoryappsubmissionawal.model.Story
import com.mastercoding.mystoryappsubmissionawal.story.adapter.StoryListAdapter
import com.mastercoding.mystoryappsubmissionawal.story.adapter.LoadingStateAdapter
import com.mastercoding.mystoryappsubmissionawal.utils.PrefManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StoryListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StoryListAdapter
    private lateinit var prefManager: PrefManager

    private val storyViewModel: StoryViewModel by viewModels {
        StoryViewModelFactory(ApiService.create())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_list)

        recyclerView = findViewById(R.id.rv_story_list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val fabAddStory: FloatingActionButton = findViewById(R.id.fab_add_story)
        fabAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }

        adapter = StoryListAdapter { story ->
            val intent = Intent(this, StoryDetailActivity::class.java).apply {
                putExtra(StoryDetailActivity.EXTRA_STORY_ID, story.id)
            }
            startActivity(intent)
        }

        recyclerView.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                observeStories()
            }
        )

        observeStories()
    }

    private fun observeStories() {
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        lifecycleScope.launch {
            storyViewModel.getStories("Bearer YOUR_TOKEN_HERE").collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        lifecycleScope.launch {
            adapter.addLoadStateListener { loadState ->
                progressBar.visibility = if (loadState.refresh is LoadState.Loading) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

                if (loadState.refresh is LoadState.Error) {
                    val error = (loadState.refresh as LoadState.Error).error
                    Toast.makeText(this@StoryListActivity, error.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                prefManager.clear()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            R.id.action_maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
