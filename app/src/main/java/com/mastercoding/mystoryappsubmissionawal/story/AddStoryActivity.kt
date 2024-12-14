package com.mastercoding.mystoryappsubmissionawal.story

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.mastercoding.mystoryappsubmissionawal.databinding.ActivityAddStoryBinding
import com.mastercoding.mystoryappsubmissionawal.utils.PrefManager
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var prefManager: PrefManager
    private var photoFile: File? = null

    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    private val addStoryViewModel: AddStoryViewModel by viewModels()

    companion object {
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        private val READ_STORAGE_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PrefManager(this)

        setupLaunchers()

        addStoryViewModel.storyRefreshLiveData.observe(this, Observer { success ->
            binding.progressBar.visibility = android.view.View.GONE

            if (success) {
                Toast.makeText(this, "Story uploaded successfully", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Failed to upload story", Toast.LENGTH_SHORT).show()
            }
        })

        binding.btnCamera.setOnClickListener { checkAndRequestPermission(CAMERA_PERMISSION) { openCamera() } }
        binding.btnChoosePhoto.setOnClickListener { checkAndRequestPermission(READ_STORAGE_PERMISSION) { openGallery() } }
        binding.buttonAdd.setOnClickListener { uploadStory() }
    }

    private fun setupLaunchers() {
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                photoFile?.let {
                    loadImageFromFile(it)
                }
            } else {
                Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
            }
        }

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                selectedImageUri?.let { uri ->
                    photoFile = uriToFile(uri)
                    loadImageFromUri(uri)
                }
            }
        }

        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val granted = it.value
                val permission = it.key
                if (granted) {
                    when (permission) {
                        CAMERA_PERMISSION -> openCamera()
                        READ_STORAGE_PERMISSION -> openGallery()
                    }
                } else {
                    Toast.makeText(this, "$permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkAndRequestPermission(permission: String, onGranted: () -> Unit) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            onGranted()
        } else {
            requestPermissionLauncher.launch(arrayOf(permission))
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = createImageFile()
        photoFile?.let {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                it
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            takePictureLauncher.launch(intent)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun createImageFile(): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = getExternalFilesDir(null)
        return try {
            File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        } catch (e: IOException) {
            Toast.makeText(this, "Error creating file: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun loadImageFromUri(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .into(binding.ivPhotoPreview)
    }

    private fun loadImageFromFile(file: File) {
        Glide.with(this)
            .load(file)
            .into(binding.ivPhotoPreview)
    }

    private fun uploadStory() {
        val description = binding.edAddDescription.text.toString()

        // Show loading indicator
        binding.progressBar.visibility = android.view.View.VISIBLE

        // If no photo is selected, just upload the description
        if (photoFile == null) {
            Toast.makeText(this, "Please add a photo", Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = android.view.View.GONE
            return
        }

        addStoryViewModel.uploadStory(photoFile, description)
    }

    private fun uriToFile(uri: Uri): File {
        val contentResolver = contentResolver
        val fileName = getFileName(uri)
        val inputStream = contentResolver.openInputStream(uri)
        val file = File(filesDir, fileName)
        val outputStream = file.outputStream()
        inputStream?.copyTo(outputStream)
        return file
    }

    private fun getFileName(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        val nameIndex = cursor?.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
        val fileName = cursor?.getString(nameIndex ?: 0)
        cursor?.close()
        return fileName ?: "unknown_file.jpg"
    }
}
