package com.mastercoding.mystoryappsubmissionawal.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mastercoding.mystoryappsubmissionawal.api.RetrofitInstance
import com.mastercoding.mystoryappsubmissionawal.databinding.ActivityLoginBinding
import com.mastercoding.mystoryappsubmissionawal.model.LoginRequest
import com.mastercoding.mystoryappsubmissionawal.model.LoginResponse
import com.mastercoding.mystoryappsubmissionawal.story.StoryListActivity
import com.mastercoding.mystoryappsubmissionawal.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var prefManager: PrefManager
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PrefManager(this)

        if (prefManager.isLoggedIn()) {
            navigateToStoryList()
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val loginRequest = LoginRequest(email, password)

                binding.loginButton.isEnabled = false
                binding.progressBar.visibility = android.view.View.VISIBLE

                RetrofitInstance.apiService.loginUser(loginRequest)
                    .enqueue(object : Callback<LoginResponse> {
                        override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                            binding.loginButton.isEnabled = true
                            binding.progressBar.visibility = android.view.View.GONE

                            if (response.isSuccessful) {
                                val loginResponse = response.body()
                                if (loginResponse?.error == false) {
                                    val token = loginResponse.loginResult?.token
                                    prefManager.saveToken(token ?: "")
                                    prefManager.setLoggedIn(true)
                                    navigateToStoryList()
                                } else {
                                    Toast.makeText(this@LoginActivity, loginResponse?.message, Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this@LoginActivity, "Login failed. Please try again.", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            binding.loginButton.isEnabled = true
                            binding.progressBar.visibility = android.view.View.GONE
                            Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            } else {
                Toast.makeText(this@LoginActivity, "Please enter both email and password.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToStoryList() {
        val intent = Intent(this@LoginActivity, StoryListActivity::class.java)
        startActivity(intent)
        finish()
    }
}
