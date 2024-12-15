package com.mastercoding.mystoryappsubmissionawal.story

import com.mastercoding.mystoryappsubmissionawal.model.Story
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mastercoding.mystoryappsubmissionawal.api.ApiService

class StoryPagingSource(private val apiService: ApiService, private val token: String) : PagingSource<Int, Story>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        val page = params.key ?: 1
        return try {
            val response = apiService.getStories(token, page)
            LoadResult.Page(
                data = response.listStory,
                prevKey = null,
                nextKey = if (response.listStory.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition
    }
}
