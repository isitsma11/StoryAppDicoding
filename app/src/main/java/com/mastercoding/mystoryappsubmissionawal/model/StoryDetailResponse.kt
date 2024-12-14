package com.mastercoding.mystoryappsubmissionawal.model

data class StoryDetailResponse(
    val error: Boolean,
    val message: String,
    val story: Story
)