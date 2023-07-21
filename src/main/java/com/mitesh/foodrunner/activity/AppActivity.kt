package com.mitesh.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.mitesh.foodrunner.*
import com.mitesh.foodrunner.fragment.*

class AppActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var app_tv_showmsg: TextView
    lateinit var app_drawerLayout: DrawerLayout
    lateinit var app_coordinatorLayout: CoordinatorLayout
    lateinit var app_toolbar: Toolbar
    lateinit var app_frameLayout: FrameLayout
    lateinit var app_navigationView: NavigationView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var drawer_name: TextView
    lateinit var drawer_email: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_app)

        app_drawerLayout = findViewById(R.id.app_drawerLayout)
        app_coordinatorLayout = findViewById(R.id.app_coordinatorLayout)
        app_toolbar = findViewById(R.id.app_toolbar)
        app_frameLayout = findViewById(R.id.app_frameLayout)
        app_navigationView = findViewById(R.id.app_navigationView)


        setUpToolbar()
        var actionBarDrawerToggle = ActionBarDrawerToggle(
            this@AppActivity,
            app_drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        app_drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        app_navigationView.setNavigationItemSelectedListener(this)

        openHOme()
    }

    fun setUpToolbar() {
        setSupportActionBar(app_toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            app_drawerLayout.openDrawer(GravityCompat.START)
            drawer_name = findViewById(R.id.drawer_name)
            drawer_email = findViewById(R.id.drawer_email)
            drawer_name.text = sharedPreferences.getString("Name", "Mitesh kr.")
            drawer_email.text = sharedPreferences.getString("Email", "example@mail.com")

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var id = item.itemId
        app_navigationView.setCheckedItem(id)

        when (id) {
            R.id.menu_faqs_app -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.app_frameLayout, AppFaqs()).commit()
                supportActionBar?.title = "FAQs"
                app_drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.menu_fav_app -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.app_frameLayout, AppFavourite()).commit()
                supportActionBar?.title = "Favourite"
                app_drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.menu_home_app -> {
                openHOme()
                app_drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.menu_profile_app -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.app_frameLayout, AppProfile()).commit()
                supportActionBar?.title = "Profile"
                app_drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.menu_order_history_app -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.app_frameLayout, OrderHistory()).commit()
                supportActionBar?.title="Order History"
                app_drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.menu_logOut_app -> {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Log out")
                dialog.setMessage("Are you sure?")
                dialog.setPositiveButton("Yes") { text, listner ->
                    sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
                    val intent = Intent(this@AppActivity, Login_Activity::class.java)

                    //app_drawerLayout.closeDrawer(GravityCompat.START)
                    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                    finish()
                }
                dialog.setNegativeButton("No") { text, listner ->

                }
                dialog.create()
                dialog.show()

            }
        }
        return true
    }

    fun openHOme() {
        val fragment = AppHome()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.app_frameLayout, fragment)
        transaction.commit()
        supportActionBar?.title = "All Restaurant"
        app_navigationView.setCheckedItem(R.id.menu_home_app)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.app_frameLayout)
        when (frag) {
            !is AppHome -> {
                openHOme()
            }
            else -> super.onBackPressed()
        }
    }
}