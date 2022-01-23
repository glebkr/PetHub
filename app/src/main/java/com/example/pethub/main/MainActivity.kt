package com.example.pethub.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.pethub.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navController = findNavController(R.id.fragment_container)
        bottomNavigationView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> findNavController(R.id.fragment_container).navigate(R.id.filterFragment)
            R.id.logOut -> {
                val sharedPrefs = this.getSharedPreferences("SharedPrefs", MODE_PRIVATE)
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