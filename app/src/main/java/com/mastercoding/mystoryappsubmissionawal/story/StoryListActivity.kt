package com.mastercoding.mystoryappsubmissionawal.story

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mastercoding.mystoryappsubmissionawal.R
import com.mastercoding.mystoryappsubmissionawal.api.ApiService
import com.mastercoding.mystoryappsubmissionawal.auth.LoginActivity
import com.mastercoding.mystoryappsubmissionawal.model.StoryResponse
import com.mastercoding.mystoryappsubmissionawal.story.adapter.StoryListAdapter
import com.mastercoding.mystoryappsubmissionawal.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryListActivity : AppCompatActivity() {

    private lateinit var prefManager: PrefManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var adapter: StoryListAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefManager = PrefManager(this)
        if (prefManager.getToken().isNullOrEmpty()) {
            redirectToLogin()
        } else {
            setContentView(R.layout.activity_story_list)

            progressBar = findViewById(R.id.progressBar)
            recyclerView = findViewById(R.id.rv_story_list)
            recyclerView.layoutManager = LinearLayoutManager(this)

            adapter = StoryListAdapter(emptyList()) { story ->
                val intent = Intent(this, StoryDetailActivity::class.java).apply {
                    putExtra(StoryDetailActivity.EXTRA_STORY_ID, story.id)
                }

                val options = android.app.ActivityOptions.makeSceneTransitionAnimation(
                    this,
                    findViewById(R.id.iv_item_photo),
                    "shared_image"
                )

                startActivity(intent, options.toBundle())
            }
            recyclerView.adapter = adapter

            fetchStories()

            fab = findViewById(R.id.fab_add_story)
            fab.setOnClickListener {
                playFabAnimation()
                fab.postDelayed({
                    val intent = Intent(this, AddStoryActivity::class.java)
                    startActivityForResult(intent, 1)
                }, 800)
            }
        }
    }

    private fun playFabAnimation() {
        val scaleX = ObjectAnimator.ofFloat(fab, "scaleX", 1f, 1.5f, 1f)
        val scaleY = ObjectAnimator.ofFloat(fab, "scaleY", 1f, 1.5f, 1f)
        val alpha = ObjectAnimator.ofFloat(fab, "alpha", 1f, 0.7f, 1f)

        val animatorSet = AnimatorSet().apply {
            duration = 700
            interpolator = AccelerateDecelerateInterpolator()
            playTogether(scaleX, scaleY, alpha)
        }
        animatorSet.start()
    }

    private fun fetchStories() {
        progressBar.visibility = ProgressBar.VISIBLE

        val token = "Bearer ${prefManager.getToken()}"
        ApiService.create().getAllStories(token, null, null, null).enqueue(object :
            Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                progressBar.visibility = ProgressBar.GONE

                if (response.isSuccessful) {
                    Log.d("StoryListActivity", "Fetched stories successfully")
                    response.body()?.listStory?.let { storyList ->
                        adapter.updateData(storyList)
                    }
                } else {
                    Log.d("StoryListActivity", "Failed to fetch stories: ${response.code()}")
                    Toast.makeText(this@StoryListActivity, "Failed to fetch stories", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                progressBar.visibility = ProgressBar.GONE
                Log.d("StoryListActivity", "API call failed: ${t.message}")
                Toast.makeText(this@StoryListActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                performLogout()
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

    private fun performLogout() {
        prefManager.clear()
        Toast.makeText(this, "Successfully logged out", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Log.d("StoryListActivity", "Activity result received, refreshing list")
            fetchStories()
        }
    }
}
