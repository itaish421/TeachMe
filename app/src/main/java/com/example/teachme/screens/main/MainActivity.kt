package com.example.teachme.screens.main

import android.content.DialogInterface
import android.content.DialogInterface.OnCancelListener
import android.content.DialogInterface.OnClickListener
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavGraph
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.teachme.LoadingState
import com.example.teachme.R
import com.example.teachme.StudentViewModel
import com.example.teachme.databinding.ActivityMainBinding
import com.example.teachme.models.Student
import com.example.teachme.models.Teacher
import com.example.teachme.screens.auth.AuthActivity
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: StudentViewModel by viewModels()

    override fun onBackPressed() {
        val navController = findNavController(R.id.mainNavGraph)
        if (!navController.popBackStack())
            super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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
                is LoadingState.Loaded -> binding.pbMain.visibility = View.GONE
                is LoadingState.Loading -> binding.pbMain.visibility = View.VISIBLE
            }
        }

        viewModel.userState.observe(this) { userResource ->
            if (userResource.loaded && userResource.data == null) {
                viewModel.userState.removeObservers(this)
                finish()
                startActivity(Intent(this, AuthActivity::class.java))
            } else if (userResource.loaded) {
                if (!viewModel.hasGraph) {
                    val navController = findNavController(R.id.mainNavGraph)
                    val navigationView = binding.bottomNavView
                    when (userResource.data) {
                        is Teacher -> {
                            navigationView.inflateMenu(R.menu.bottom_nav_teacher)
                            navController.navInflater.inflate(R.navigation.main_nav_graph_teacher)
                        }

                        is Student -> {
                            navigationView.inflateMenu(R.menu.bottom_nav_student)
                            navController.navInflater.inflate(R.navigation.main_nav_graph_student)
                        }

                        else -> {
                            null
                        }
                    }?.let {
                        navController.graph = it
                    }
                    NavigationUI.setupWithNavController(navigationView, navController)

                    viewModel.hasGraph = true
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_navigation, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.logoutTopNav) {
            AlertDialog.Builder(this)
                .setTitle("TeachMe")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton(
                    "Yes"
                ) { _, _ ->
                    viewModel.logOut()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        return super.onOptionsItemSelected(item)
    }
}