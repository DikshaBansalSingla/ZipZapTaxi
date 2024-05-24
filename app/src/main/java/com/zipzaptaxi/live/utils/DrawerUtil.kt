package com.zipzaptaxi.live.utils

import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.zipzaptaxi.live.home.MainActivity


object DrawerUtils {
   /* fun openCloseDrawer(activity: MainActivity) {
        if (activity.binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            activity.binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            activity.binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }*/

    fun openDrawer(drawerLayout: DrawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START)
    }

    fun closeDrawer(drawerLayout: DrawerLayout) {
        drawerLayout.closeDrawer(GravityCompat.START)
    }
}

