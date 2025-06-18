package com.zipzaptaxi.live.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.adapter.ClickOnItem
import com.zipzaptaxi.live.adapter.ItemAdapter
import com.zipzaptaxi.live.cache.CacheConstants
import com.zipzaptaxi.live.cache.getUser
import com.zipzaptaxi.live.databinding.ActivityMainBinding
import com.zipzaptaxi.live.model.DrawerModel
import com.zipzaptaxi.live.utils.extensionfunctions.firstCap


class MainActivity : AppCompatActivity(),
    ClickOnItem {

    private lateinit var drawerRecyclerView: RecyclerView
    private lateinit var tvUserType: TextView
    val items = ArrayList<DrawerModel>()

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    var navigation: NavigationView? = null
    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideShowItem()
//        addItemsForVendor()
//        addItemsForDriver()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        /*binding.ivDrawer.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }*/
        // Initialize NavController with the NavHostFragment
        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
        navController= navHostFragment.navController
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph)

        navHostFragment.navController.graph = graph
        val headerView = binding.navView.getHeaderView(0)
        tvUserType= headerView.findViewById(R.id.tvUserType)
        tvUserType.text= getUser(this).user_type?.firstCap()

       // binding.navView.setNavigationItemSelectedListener(this)


    }

    private fun addItemsForVendor() {
        items.add(DrawerModel("Home",R.drawable.home))
        items.add(DrawerModel("Profile",R.drawable.user))
        items.add(DrawerModel("Add Home City",R.drawable.baseline_add_circle_24))
        items.add(DrawerModel("My Bookings",R.drawable.vehicle_list))
        items.add(DrawerModel("Wallet",R.drawable.wallet))
        items.add(DrawerModel("Bank Details",R.drawable.specification))
        items.add(DrawerModel("Documents",R.drawable.documents))
        items.add(DrawerModel("Vehicle List",R.drawable.vehicle_list))
        items.add(DrawerModel("Driver List",R.drawable.add_user))
        items.add(DrawerModel("Notifications",R.drawable.notification))
        items.add(DrawerModel("Settings",R.drawable.add_user))
        items.add(DrawerModel("Support",R.drawable.baseline_contact_support_24))
        items.add(DrawerModel("Share App",R.drawable.shared))
        setAdapter()
    }

    private fun addItemsForDriver() {
        items.add(DrawerModel("Home",R.drawable.home))
        items.add(DrawerModel("Profile",R.drawable.user))
        items.add(DrawerModel("Add Home City",R.drawable.baseline_add_circle_24))
        items.add(DrawerModel("My Bookings",R.drawable.vehicle_list))
       // items.add(DrawerModel("Wallet",R.drawable.wallet))
        items.add(DrawerModel("Bank Details",R.drawable.specification))
      //  items.add(DrawerModel("Documents",R.drawable.documents))
      //  items.add(DrawerModel("Vehicle List",R.drawable.vehicle_list))
      //  items.add(DrawerModel("Driver List",R.drawable.add_user))
        items.add(DrawerModel("Notifications",R.drawable.notification))
        items.add(DrawerModel("Settings",R.drawable.add_user))
        items.add(DrawerModel("Support",R.drawable.baseline_contact_support_24))
        items.add(DrawerModel("Share App",R.drawable.shared))
        setAdapter()
    }

    private fun setAdapter() {
        val headerView = binding.navView.getHeaderView(0)
        drawerRecyclerView = headerView.findViewById(R.id.drawerRecyclerView)
        drawerRecyclerView.layoutManager = LinearLayoutManager(this)
        drawerRecyclerView.adapter = ItemAdapter(this,items, this)
    }

    private fun hideShowItem() {
        navigation = findViewById<NavigationView>(R.id.nav_view)
        val nav_Menu = navigation?.menu

        val userType = getUser(this).user_type
        Log.d("UserType", "User type is: $userType")

        if (userType == "driver") {
            addItemsForDriver()
            /*Log.d("HideItems", "Hiding wallet, documents, vehicle list, and driver list for driver.")
            nav_Menu?.findItem(R.id.wallet)?.isVisible = false
            nav_Menu?.findItem(R.id.documents)?.isVisible = false
            nav_Menu?.findItem(R.id.vehicleList)?.isVisible = false
            nav_Menu?.findItem(R.id.driverList)?.isVisible = false

            // Force menu refresh
            navigation?.menu?.clear()
            navigation?.inflateMenu(R.menu.navigation_menu)
          //  invalidateOptionsMenu() // Refresh the menu to apply visibility changes*/
        }else{
            addItemsForVendor()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation_menu, menu)
        return true

    }

    override fun onBackPressed() {
        var toast= Toast(this)
        val mFragmentManager = supportFragmentManager
        val frag: Fragment? = mFragmentManager.findFragmentById(R.id.nav_host_fragment)

        when {
            binding.drawerLayout.isDrawerOpen(GravityCompat.START) -> binding.drawerLayout.closeDrawer(Gravity.LEFT)

            else -> {
                when (CacheConstants.Current) {
                    "home" -> {
                        // Set the time window for double back press (e.g., 2 seconds)
                        if (backPressedTime + 2000 > System.currentTimeMillis()) {
                            toast.cancel()
                            finishAffinity()
                            return
                        } else {
                            // Show a toast message prompting the user to press again to exit
                            toast = Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT)
                            toast.show()
                        }
                        backPressedTime = System.currentTimeMillis()
                      //  finishAffinity()
                        // It's a LoginFragment
                    }
                    "profile" -> {
                        val options = NavOptions.Builder()
                            .setPopUpTo(R.id.homeFragment, true)
                            .build()
                        findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment, null, options)
                    }
                    "addHome" -> {
                        val options = NavOptions.Builder()
                            .setPopUpTo(R.id.homeFragment, true)
                            .build()
                        findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment, null, options)
                    }
                    "bankDetail" -> {
                        val options = NavOptions.Builder()
                            .setPopUpTo(R.id.homeFragment, true)
                            .build()
                        findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment, null, options)
                    }
                    "bookings" -> {
                        val options = NavOptions.Builder()
                            .setPopUpTo(R.id.homeFragment, true)
                            .build()
                        findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment, null, options)
                    }
                    "wallet" -> {
                        val options = NavOptions.Builder()
                            .setPopUpTo(R.id.homeFragment, true)
                            .build()
                        findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment, null, options)
                    }
                    "docs" -> {
                        val options = NavOptions.Builder()
                            .setPopUpTo(R.id.homeFragment, true)
                            .build()
                        findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment, null, options)
                    }
                    "vehicleList" -> {
                        val options = NavOptions.Builder()
                            .setPopUpTo(R.id.homeFragment, true)
                            .build()
                        findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment, null, options)
                    }
                    "driverList" -> {
                        val options = NavOptions.Builder()
                            .setPopUpTo(R.id.homeFragment, true)
                            .build()
                        findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment, null, options)
                    }
                    "notifications" -> {
                        val options = NavOptions.Builder()
                            .setPopUpTo(R.id.homeFragment, true)
                            .build()
                        findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment, null, options)
                    }
                    "settings" -> {
                        val options = NavOptions.Builder()
                            .setPopUpTo(R.id.homeFragment, true)
                            .build()
                        findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment, null, options)
                    }
                    "support" -> {
                        val options = NavOptions.Builder()
                            .setPopUpTo(R.id.homeFragment, true)
                            .build()
                        findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment, null, options)
                    }
                    "driverDetail" -> {

                        navController.popBackStack(R.id.driverListFragment, false)

                    }
                    "driver" -> {
                        navController.popBackStack(R.id.driverListFragment, false)

                    }

                    "bookingDetail" -> {
                        navController.popBackStack()
                       // navController.popBackStack(R.id.homeFragment, false)

                    }
                    "cabFree" -> {
                        navController.popBackStack(R.id.homeFragment, false)

                    }
                    "paidAmt" -> {
                        navController.popBackStack(R.id.walletFragment, false)

                    }
                    "tripAmt" -> {
                        navController.popBackStack(R.id.walletFragment, false)

                    }
                    else -> {
                        super.onBackPressed()
                    }
                }
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

    override fun onDrawerItemClick(position: Int, items: ArrayList<DrawerModel>) {
      //  binding.drawerLayout.closeDrawer(GravityCompat.START)
        when (items[position].item) {
            "Home" -> {
                navController.navigate(R.id.homeFragment)
                openCloseDrawer()
            }
            "Profile"-> {
                navController.navigate(R.id.profileFragment)
                openCloseDrawer()
            }
            "Add Home City" -> {
                //navController.navigate(R.id.bookingsFragment)
                navController.navigate(R.id.addHomeCityFragment)
                openCloseDrawer()
            }
            "My Bookings" -> {
                navController.navigate(R.id.bookingsFragment)
                // navController.navigate(R.id.bankDetailFragment)
                openCloseDrawer()
            }
            "Wallet" -> {
                navController.navigate(R.id.walletFragment)
                openCloseDrawer()
            }
            "Bank Details" -> {
                //  navController.navigate(R.id.uploadDocumentsFragment)
                navController.navigate(R.id.bankDetailFragment)
                openCloseDrawer()
            }
            "Documents" -> {
                // navController.navigate(R.id.driverListFragment)
                navController.navigate(R.id.uploadDocumentsFragment)
                openCloseDrawer()
            }
            "Vehicle List" -> {
                navController.navigate(R.id.vehicleListFragment)
                openCloseDrawer()

            }
            "Driver List" -> {
                navController.navigate(R.id.driverListFragment)
                openCloseDrawer()
            }
            "Notifications" -> {
                navController.navigate(R.id.notificationFragment)
                openCloseDrawer()
            }
            "Settings" -> {
                navController.navigate(R.id.settingsFragment)
                openCloseDrawer()
            }
            "Support" -> {
                navController.navigate(R.id.supportFragment)
                openCloseDrawer()
            }
            "Share App" -> {
                val appPackageName = "com.zipzaptaxi.live" // gets your app's package name
                val playStoreLink = "https://play.google.com/store/apps/details?id=$appPackageName"
                val referralCode = getUser(this).self_referal_code // Replace with the actual referral code

                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "Zipzap Taxi")
                intent.putExtra(Intent.EXTRA_TEXT, "Install the app using my referral code: $referralCode\n\n$playStoreLink")
                startActivity(Intent.createChooser(intent, "choose one"))

            }
        }

    }

}