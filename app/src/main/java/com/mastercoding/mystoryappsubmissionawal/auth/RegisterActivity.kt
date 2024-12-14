package com.mastercoding.mystoryappsubmissionawal.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mastercoding.mystoryappsubmissionawal.api.RetrofitInstance
import com.mastercoding.mystoryappsubmissionawal.model.RegisterRequest
import com.mastercoding.mystoryappsubmissionawal.model.RegisterResponse
import com.mastercoding.mystoryappsubmissionawal.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                val registerRequest = RegisterRequest(name, email, password)

                binding.btnRegister.isEnabled = false
                binding.progressBar.visibility = android.view.View.VISIBLE

                RetrofitInstance.apiService.registerUser(registerRequest)
                    .enqueue(object : Callback<RegisterResponse> {
                        override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                            binding.btnRegister.isEnabled = true
                            binding.progressBar.visibility = android.view.View.GONE

                            if (response.isSuccessful) {
                                val registerResponse = response.body()
                                Toast.makeText(this@RegisterActivity, registerResponse?.message, Toast.LENGTH_SHORT).show()

                                goToLoginPage()
                            } else {
                                Toast.makeText(this@RegisterActivity, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                            binding.btnRegister.isEnabled = true
                            binding.progressBar.visibility = android.view.View.GONE
                            Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            } else {
                Toast.makeText(this@RegisterActivity, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvLogin.setOnClickListener {
            goToLoginPage()
        }
    }

    private fun goToLoginPage() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
