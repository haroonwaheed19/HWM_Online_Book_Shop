package com.hwm.hwmonlinebookshop

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserDashboard : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)
        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        drawerLayout = findViewById(R.id.drawerLayout)
        val navigationView: NavigationView = findViewById(R.id.navigationView)

        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)

        toolbar.setNavigationIcon(R.drawable.ic_menu)
        toolbar.setNavigationOnClickListener {
            drawerLayout.open()
        }

        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.bottom_cart -> {
                    loadFragment(CartFragment())
                    true
                }
                R.id.bottom_search -> {
                    loadFragment(SearchFragment())
                    true
                }
                R.id.bottom_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        navigationView.setNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                R.id.nav_cart -> {
                    loadFragment(CartFragment())
                    true
                }
                R.id.nav_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                R.id.nav_logout -> {
                    auth.signOut()
                    startActivity(Intent(this, LoginScreen::class.java))
                    finish()
                    true
                }
                else -> false
            }.also {
                drawerLayout.closeDrawer(navigationView)
            }
        }

        val currentUser = auth.currentUser
        currentUser?.let {
            val userId = it.uid
            val headerView: View = navigationView.getHeaderView(0)
            val navUserName = headerView.findViewById<TextView>(R.id.nav_header_name)
            val navUserEmail = headerView.findViewById<TextView>(R.id.nav_header_email)

            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name") ?: "User Name"
                        val email = document.getString("email") ?: "user@example.com"
                        navUserName.text = name
                        navUserEmail.text = email
                    } else {
                        navUserName.text = "User Name"
                        navUserEmail.text = "user@example.com"
                    }
                }
                .addOnFailureListener {
                    navUserName.text = "User Name"
                    navUserEmail.text = "user@example.com"
                }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainContent)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_app_bar_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search Books..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return true
    }

    fun addToCart(book: Book) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val cartRef = firestore.collection("carts").document(user.uid)
                .collection("items").document(book.id)

            cartRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    // Update existing cart item
                    val cartItem = document.toObject(CartItem::class.java)
                    cartItem?.let {
                        it.quantity += 1
                        val originalPrice = book.price.toDouble()
                        it.price = (originalPrice * it.quantity).toString()
                        cartRef.set(it)
                    }
                } else {
                    // Add new cart item
                    val newCartItem = CartItem(
                        id = book.id,
                        name = book.name,
                        description = book.description,
                        author = book.author,
                        price = book.price,
                        quantity = 1,
                        imageUrl = book.imageUrl
                    )
                    cartRef.set(newCartItem)
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to add to cart: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("UserDashboard", "Error adding to cart", exception)
            }
        } ?: run {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}