package com.zipzaptaxi.live.home

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationView
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.cache.getUser
import com.zipzaptaxi.live.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    var navigation: NavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize NavController with the NavHostFragment
        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
        navController= navHostFragment.navController
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph)
        navHostFragment.navController.graph = graph

        binding.navView.setNavigationItemSelectedListener(this)
        hideShowItem()

    }
    private fun hideShowItem() {
        navigation = findViewById<View>(R.id.nav_view) as NavigationView
        val nav_Menu = navigation?.menu

        //for user navigation fragments
        if (getUser(this).user_type == "driver") {
            nav_Menu?.findItem(R.id.wallet)?.isVisible = false
            nav_Menu?.findItem(R.id.documents)?.isVisible = false

            nav_Menu?.findItem(R.id.vehicleList)?.isVisible = false
            nav_Menu?.findItem(R.id.driverList)?.isVisible = false


        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation_menu, menu)
        return true

    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation item clicks
        when (item.itemId) {
            R.id.home ->{

                navController.navigate(R.id.homeFragment)
                openCloseDrawer()
            }
            R.id.profile ->{
                navController.navigate(R.id.profileFragment)
                openCloseDrawer()
            }
            R.id.bookings -> {
                navController.navigate(R.id.bookingsFragment)
                openCloseDrawer()
            }

            R.id.wallet -> {
                navController.navigate(R.id.walletFragment)
                openCloseDrawer()
            }

            R.id.documents -> {
                navController.navigate(R.id.uploadDocumentsFragment)
                openCloseDrawer()
            }
            R.id.driverList -> {
                navController.navigate(R.id.driverListFragment)
                openCloseDrawer()
            }
            R.id.shareApp -> {
                // Handle share app action
                val intent = Intent(Intent.ACTION_SEND)
                intent.setType("text/plain")
                intent.putExtra(Intent.EXTRA_SUBJECT, "Zipzap Taxi")
                intent.putExtra(Intent.EXTRA_TEXT, "Install the App")
                startActivity(Intent.createChooser(intent, "choose one"))
            }

            R.id.vehicleList -> {
                navController.navigate(R.id.vehicleListFragment)
                openCloseDrawer()
            }
            R.id.settings -> {
                navController.navigate(R.id.settingsFragment)
                openCloseDrawer()
            }
            R.id.notifications -> {
                navController.navigate(R.id.notificationFragment)
                openCloseDrawer()
            }
            R.id.addHomeCity -> {
                navController.navigate(R.id.addHomeCityFragment)
                openCloseDrawer()
            }
            R.id.support -> {
                navController.navigate(R.id.supportFragment)
                openCloseDrawer()
            }
        }

        return true
    }


    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            // Close the drawer if it's open
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // Handle back press using NavController
            if (!navController.popBackStack()) {
                super.onBackPressed()
            }
        }
    }
    fun openCloseDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(Gravity.LEFT)
        } else {
            binding.drawerLayout.openDrawer(Gravity.LEFT)
        }
    }

}