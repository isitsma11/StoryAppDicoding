package com.mastercoding.mystoryappsubmissionawal.story

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "story_table")
data class StoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val photoUrl: String?,
    val description: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val lat: Float?,
    val lon: Float?
)
