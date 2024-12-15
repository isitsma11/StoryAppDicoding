package com.mastercoding.mystoryappsubmissionawal.story.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mastercoding.mystoryappsubmissionawal.databinding.ItemStoryBinding
import com.mastercoding.mystoryappsubmissionawal.model.Story

class StoryViewHolder(
    private val binding: ItemStoryBinding,
    private val onClick: (Story) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(story: Story) {
        binding.tvItemName.text = story.name
        binding.tvItemDescription.text = story.description ?: "No description available"

        Glide.with(binding.ivItemPhoto.context)
            .load(story.photoUrl)
            .placeholder(android.R.drawable.ic_menu_report_image) // You can replace this placeholder image
            .into(binding.ivItemPhoto)

        itemView.setOnClickListener { onClick(story) }
    }
}
