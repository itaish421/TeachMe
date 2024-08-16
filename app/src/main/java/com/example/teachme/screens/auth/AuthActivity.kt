package com.example.teachme.screens.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.teachme.AuthViewModel
import com.example.teachme.LoadingState
import com.example.teachme.R
import com.example.teachme.databinding.ActivityAuthBinding
import com.example.teachme.screens.main.MainActivity
import com.google.android.material.snackbar.Snackbar

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.exceptionsState.observe(this) {
            Snackbar.make(
                binding.root,
                it.message ?: "Unknown error occurred",
                Snackbar.LENGTH_LONG
            ).show()
        }

        viewModel.loadingState.observe(this) {
            when (it) {
                is LoadingState.Loaded -> binding.pbAuth.visibility = View.GONE
                is LoadingState.Loading -> binding.pbAuth.visibility = View.VISIBLE
            }
        }

        viewModel.userState.observe(this) {
            it.data?.let {
                viewModel.userState.removeObservers(this)
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }
}