package com.mastercoding.mystoryappsubmissionawal.story

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.paging.PagingSource

@Dao
interface StoryDao {
    @Insert
    suspend fun insert(story: StoryEntity)

    @Insert
    suspend fun insertAll(stories: List<StoryEntity>)

    @Delete
    suspend fun delete(story: StoryEntity)

    @Query("SELECT * FROM story_table ORDER BY createdAt DESC")
    fun getAllStories(): PagingSource<Int, StoryEntity> // Paging 3 support
}
