package com.example.pethub.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toolbar
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.example.pethub.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val viewModel by viewModels<com.example.pethub.viewmodel.ViewModel>()
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = findNavController(R.id.fragment_container)
        bottomNavigationView.setupWithNavController(navController)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.favoriteFragment, R.id.addFragment, R.id.mapsFragment, R.id.profileFragment,R.id.loginFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)
        val sharedPrefs = this.getSharedPreferences("SharedPrefs", MODE_PRIVATE)
        sharedPrefs.edit().apply {
            putString("query", "")
            putString("type", "")
            putString("kind", "")
            putString("x_coord", "")
            putString("y_coord", "")
            putBoolean("add", false)
        }.apply()

    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val sharedPrefs = this.getSharedPreferences("SharedPrefs", MODE_PRIVATE)
        when (item.itemId) {
            R.id.filter -> findNavController(R.id.fragment_container).navigate(R.id.filterFragment)
            R.id.clear_filter_home -> {
                sharedPrefs.edit().apply {
                    putString("query", "")
                    putString("type", "")
                    putString("kind", "")
                    putString("cityFilter", "")
                }.apply()
                viewModel._adList.postValue(null)
                findNavController(R.id.fragment_container).navigate(R.id.homeFragment)

            }
            R.id.filter_clear -> {
                sharedPrefs.edit().apply {
                    putString("query", "")
                    putString("type", "")
                    putString("kind", "")
                    putString("cityFilter", "")
                }.apply()
                viewModel._adList.postValue(null)
                findNavController(R.id.fragment_container).navigate(R.id.filterFragment)
            }
            R.id.edit_profile -> findNavController(R.id.fragment_container).navigate(R.id.editProfileFragment)
            R.id.logOut -> {
                sharedPrefs
                    .edit()
                    .putString("token", "")
                    .apply()
                findNavController(R.id.fragment_container).navigate(R.id.loginFragment)
            }

        }
        return super.onOptionsItemSelected(item)
    }


}