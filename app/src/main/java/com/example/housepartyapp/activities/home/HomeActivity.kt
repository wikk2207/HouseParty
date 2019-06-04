package com.example.housepartyapp.activities.home

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.housepartyapp.R
import android.support.design.widget.NavigationView
import android.support.v7.widget.Toolbar
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v4.view.GravityCompat
import android.view.MenuItem
import android.app.Activity
import android.content.Intent
import com.example.housepartyapp.activities.profile.ProfileFragment
import com.example.housepartyapp.activities.arrival.ArrivalFragment
import com.example.housepartyapp.activities.dishes.DishesFragment
import com.example.housepartyapp.activities.finances.FinanceFragment
import com.example.housepartyapp.activities.friends.FriendsFragment
import com.example.housepartyapp.activities.guests.organizator.GuestListAsOrganizerFragment

import com.example.housepartyapp.activities.notifications.NotificationsFragment
import com.example.housepartyapp.activities.organizer.OrganizerFragment
import com.example.housepartyapp.activities.party.PartyFragment
import com.example.housepartyapp.activities.shopping.ShoppingFragment
import com.example.housepartyapp.activities.summary.guest.SummaryFragmentGuest


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_container,
            PartyFragment()
        ).commit()

    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            backNavigation()
    }

    private fun backNavigation() {
        val fragments = supportFragmentManager.fragments
        for (f in fragments) {
            if (f != null) {
                when (f) {
                    is ArrivalFragment -> f.onBackPressed()
                    is DishesFragment -> f.onBackPressed()
                    is FinanceFragment -> f.onBackPressed()
                    is FriendsFragment -> f.onBackPressed()
                    is GuestListAsOrganizerFragment -> f.onBackPressed()
                    is NotificationsFragment -> f.onBackPressed()
                    is OrganizerFragment -> f.onBackPressed()
                    is ProfileFragment -> f.onBackPressed()
                    is ShoppingFragment -> f.onBackPressed()
                    is SummaryFragmentGuest -> f.onBackPressed()
                    is PartyFragment -> {
                        super.onBackPressed()
                        f.onBackPressed()
                    }
                    else -> super.onBackPressed()
                }
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_parties -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    PartyFragment()
                ).commit()
            }
            R.id.nav_friends -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    FriendsFragment()
                ).commit()
            }
            R.id.nav_notifications -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    NotificationsFragment()
                ).commit()
            }
            R.id.nav_settings -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    ProfileFragment()
                ).commit()
            }
            R.id.nav_logout -> {
                val myintent = Intent()
                setResult(Activity.RESULT_OK, myintent)
                finish()
            }
        }


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


}
