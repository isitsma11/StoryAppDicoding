package com.mastercoding.mystoryappsubmissionawal

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mastercoding.mystoryappsubmissionawal.api.ApiService
import com.mastercoding.mystoryappsubmissionawal.auth.LoginActivity
import com.mastercoding.mystoryappsubmissionawal.story.MapsActivity
import com.mastercoding.mystoryappsubmissionawal.story.StoryViewModel
import com.mastercoding.mystoryappsubmissionawal.story.StoryViewModelFactory
import com.mastercoding.mystoryappsubmissionawal.story.adapter.StoryListAdapter
import com.mastercoding.mystoryappsubmissionawal.story.adapter.LoadingStateAdapter
import com.mastercoding.mystoryappsubmissionawal.utils.PrefManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StoryListAdapter
    private lateinit var prefManager: PrefManager

    private val storyViewModel: StoryViewModel by viewModels {
        StoryViewModelFactory(ApiService.create())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_story_list)

        prefManager = PrefManager(this)

        recyclerView = findViewById(R.id.rv_story_list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = StoryListAdapter { story ->
            Toast.makeText(this, "Clicked on: ${story.name}", Toast.LENGTH_SHORT).show()
        }

        recyclerView.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                observeStories()
            }
        )

        observeStories()
    }

    private fun observeStories() {
        val token = prefManager.getToken()

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "User is not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            storyViewModel.getStories("Bearer $token").collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        lifecycleScope.launch {
            adapter.addLoadStateListener { loadState ->

                if (loadState.refresh is LoadState.Error) {
                    val error = (loadState.refresh as LoadState.Error).error
                    Toast.makeText(this@MainActivity, error.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
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
