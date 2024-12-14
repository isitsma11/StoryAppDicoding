package com.mastercoding.mystoryappsubmissionawal.story

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.mastercoding.mystoryappsubmissionawal.api.ApiService
import com.mastercoding.mystoryappsubmissionawal.databinding.ActivityStoryDetailBinding
import com.mastercoding.mystoryappsubmissionawal.model.StoryDetailResponse
import com.mastercoding.mystoryappsubmissionawal.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryDetailBinding
    private lateinit var prefManager: PrefManager

    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.requestFeature(android.view.Window.FEATURE_ACTIVITY_TRANSITIONS)

        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val transitionName = intent.getStringExtra("shared_image")
        binding.ivDetailPhoto.transitionName = transitionName

        prefManager = PrefManager(this)
        val storyId = intent.getStringExtra(EXTRA_STORY_ID)

        binding.progressBar.visibility = android.view.View.VISIBLE

        storyId?.let {
            fetchStoryDetail(it)
        }
    }

    private fun fetchStoryDetail(id: String) {
        val token = "Bearer ${prefManager.getToken()}"
        ApiService.create().getStoryDetail(token, id).enqueue(object : Callback<StoryDetailResponse> {
            override fun onResponse(call: Call<StoryDetailResponse>, response: Response<StoryDetailResponse>) {

                binding.progressBar.visibility = android.view.View.GONE

                if (response.isSuccessful) {
                    response.body()?.story?.let { story ->
                        binding.tvDetailName.text = story.name
                        Glide.with(this@StoryDetailActivity).load(story.photoUrl).into(binding.ivDetailPhoto)
                        binding.tvDetailDescription.text = story.description
                    }
                } else {
                    Toast.makeText(this@StoryDetailActivity, "Failed to fetch story details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<StoryDetailResponse>, t: Throwable) {
                binding.progressBar.visibility = android.view.View.GONE
                Toast.makeText(this@StoryDetailActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
