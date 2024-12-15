package com.mastercoding.mystoryappsubmissionawal.testing

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import com.mastercoding.mystoryappsubmissionawal.model.Story
import com.mastercoding.mystoryappsubmissionawal.story.StoryViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import androidx.recyclerview.widget.DiffUtil
import org.junit.After

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyViewModel: StoryViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {

        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {

        Dispatchers.resetMain()
    }

    @Test
    fun `when Get Stories should return data and not null`() = runTest {
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXlqNXBjX0xBUkNfQWdLNjEiLCJpYXQiOjE2NDE3OTk5NDl9.flEMaQ7zsdYkxuyGbiXjEDXO8kuDTcI__3UjCwt6R_I"  // Provide the token here

        val dummyStory = Story(
            id = "1",
            description = "Description of story",
            createdAt = "2024-12-15T11:11:11.111Z",
            lat = 0.0f,
            lon = 0.0f,
            name = "User",
            photoUrl = "https://story-api.example.com/images/stories/photo1.png",
            updatedAt = "2024-12-15T11:11:11.111Z"
        )

        val pagingData = PagingData.from(listOf(dummyStory))

        Mockito.`when`(storyViewModel.getStories(token)).thenReturn(flow { emit(pagingData) })

        val storiesFlow: Flow<PagingData<Story>> = storyViewModel.getStories(token)

        val differ = AsyncPagingDataDiffer(
            diffCallback = DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = testDispatcher
        )

        storiesFlow.collectLatest {
            differ.submitData(it)
        }

        assertNotNull(differ.snapshot())
        assertEquals(1, differ.snapshot().size)
        assertEquals("User", differ.snapshot()[0]?.name)
    }

    @Test
    fun `when no stories should return empty data`() = runTest {
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXlqNXBjX0xBUkNfQWdLNjEiLCJpYXQiOjE2NDE3OTk5NDl9.flEMaQ7zsdYkxuyGbiXjEDXO8kuDTcI__3UjCwt6R_I"  // Provide the token here
        val pagingData = PagingData.from(emptyList<Story>())

        Mockito.`when`(storyViewModel.getStories(token)).thenReturn(flow { emit(pagingData) })


        val storiesFlow: Flow<PagingData<Story>> = storyViewModel.getStories(token)

        val differ = AsyncPagingDataDiffer(
            diffCallback = DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = testDispatcher
        )

        storiesFlow.collectLatest {
            differ.submitData(it)
        }

        assertNotNull(differ.snapshot())
        assertEquals(0, differ.snapshot().size)
    }
}

val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
    override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
        return oldItem == newItem
    }
}

val noopListUpdateCallback = object : androidx.recyclerview.widget.ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}
