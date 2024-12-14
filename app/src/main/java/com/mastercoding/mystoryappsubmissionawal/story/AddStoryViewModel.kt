package com.mastercoding.mystoryappsubmissionawal.story

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mastercoding.mystoryappsubmissionawal.api.ApiService
import com.mastercoding.mystoryappsubmissionawal.model.AddStoryResponse
import com.mastercoding.mystoryappsubmissionawal.utils.PrefManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException

class AddStoryViewModel(application: Application) : AndroidViewModel(application) {

    private val prefManager = PrefManager(application)
    private val _storyRefreshLiveData = MutableLiveData<Boolean>()
    val storyRefreshLiveData: LiveData<Boolean> = _storyRefreshLiveData

    fun uploadStory(photoFile: File?, description: String) {
        // Check if photoFile is null
        if (photoFile == null) {
            Toast.makeText(getApplication(), "Please add a photo", Toast.LENGTH_SHORT).show()
            return // Stay on the AddStory screen
        }

        // Check if the file is a valid image
        if (!photoFile.exists() || !photoFile.canRead()) {
            Toast.makeText(getApplication(), "Invalid image file", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if description is empty
        if (description.isEmpty()) {
            Toast.makeText(getApplication(), "Description cannot be empty", Toast.LENGTH_SHORT).show()
            return // Stay on the AddStory screen
        }

        // Compress and resize the image file
        val compressedFile = try {
            compressAndResizeImage(photoFile)
        } catch (e: Exception) {
            Toast.makeText(getApplication(), "Add a Photo: ${e.message}", Toast.LENGTH_SHORT).show()
            _storyRefreshLiveData.value = false
            return
        }

        val token = "Bearer ${prefManager.getToken()}"

        // Prepare the request body for the API call
        val descriptionRequestBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val photoRequestBody = compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val photoMultipart = MultipartBody.Part.createFormData("photo", compressedFile.name, photoRequestBody)

        // Make the API call to upload the story
        ApiService.create().addNewStory(token, descriptionRequestBody, photoMultipart, null, null)
            .enqueue(object : Callback<AddStoryResponse> {
                override fun onResponse(call: Call<AddStoryResponse>, response: Response<AddStoryResponse>) {
                    if (response.isSuccessful) {
                        _storyRefreshLiveData.value = true
                        Toast.makeText(getApplication(), "Story uploaded successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        _storyRefreshLiveData.value = false
                        Toast.makeText(getApplication(), "Failed to upload story", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                    _storyRefreshLiveData.value = false
                    Toast.makeText(getApplication(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun compressAndResizeImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            ?: throw FileNotFoundException("Add a Photo: ${file.absolutePath}")

        val maxWidth = 800
        val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
        val newWidth = if (bitmap.width > maxWidth) maxWidth else bitmap.width
        val newHeight = (newWidth / ratio).toInt()
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false)

        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream)
        val byteArray = outputStream.toByteArray()

        val compressedFile = File(getApplication<Application>().cacheDir, "compressed_${file.name}")
        compressedFile.writeBytes(byteArray)

        return compressedFile
    }
}
