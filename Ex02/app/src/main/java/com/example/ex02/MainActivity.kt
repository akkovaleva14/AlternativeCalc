package com.example.ex02

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.logger.Logger

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        Logger.i(message = "MainActivity created")

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onStart() {
        super.onStart()
        Logger.i(message = "MainActivity started")
    }

    override fun onResume() {
        super.onResume()
        Logger.i(message = "MainActivity resumed")
    }

    override fun onPause() {
        super.onPause()
        Logger.i(message = "MainActivity paused")
    }

    override fun onStop() {
        super.onStop()
        Logger.i(message = "MainActivity stopped")
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.i(message = "MainActivity destroyed")
    }

    override fun onSupportNavigateUp(): Boolean {
        Logger.i(message = "Navigating up")
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
