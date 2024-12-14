package com.mastercoding.mystoryappsubmissionawal.story.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mastercoding.mystoryappsubmissionawal.R
import com.mastercoding.mystoryappsubmissionawal.databinding.ItemStoryBinding
import com.mastercoding.mystoryappsubmissionawal.model.Story

class StoryListAdapter(
    private var storyList: List<Story>,
    private val onItemClick: (Story) -> Unit
) : RecyclerView.Adapter<StoryListAdapter.StoryViewHolder>() {

    class StoryViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvName = binding.tvItemName
        val ivPhoto = binding.ivItemPhoto
        val tvDescription = binding.tvItemDescription

        fun bind(story: Story, onItemClick: (Story) -> Unit) {
            tvName.text = story.name
            tvDescription.text = story.description ?: "No description available"

            Glide.with(ivPhoto.context)
                .load(story.photoUrl)
                .placeholder(R.drawable.placeholderimg)
                .into(ivPhoto)

            itemView.setOnClickListener { onItemClick(story) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = storyList[position]
        holder.bind(story, onItemClick)
    }

    override fun getItemCount(): Int = storyList.size

    fun updateData(newStoryList: List<Story>) {
        val diffCallback = StoryDiffCallback(storyList, newStoryList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        storyList = newStoryList
        diffResult.dispatchUpdatesTo(this)
    }

    class StoryDiffCallback(
        private val oldList: List<Story>,
        private val newList: List<Story>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
