package com.mastercoding.mystoryappsubmissionawal.api

import com.mastercoding.mystoryappsubmissionawal.model.AddStoryResponse
import com.mastercoding.mystoryappsubmissionawal.model.StoryResponse
import com.mastercoding.mystoryappsubmissionawal.model.LoginRequest
import com.mastercoding.mystoryappsubmissionawal.model.LoginResponse
import com.mastercoding.mystoryappsubmissionawal.model.RegisterRequest
import com.mastercoding.mystoryappsubmissionawal.model.RegisterResponse
import com.mastercoding.mystoryappsubmissionawal.model.StoryDetailResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {

    @POST("register")
    fun registerUser(
        @Body requestBody: RegisterRequest
    ): Call<RegisterResponse>

    @POST("login")
    fun loginUser(
        @Body loginRequest: LoginRequest
    ): Call<LoginResponse>

    @Multipart
    @POST("stories")
    fun addNewStory(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part?,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): Call<AddStoryResponse>

    @GET("stories")
    fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int?,
        @Query("size") size: Int?,
        @Query("location") location: Int?
    ): Call<StoryResponse>

    @GET("stories/{id}")
    fun getStoryDetail(
        @Header("Authorization") token: String,
        @Path("id") storyId: String
    ): Call<StoryDetailResponse>

    companion object {
        private const val BASE_URL = "https://story-api.dicoding.dev/v1/"

        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}
