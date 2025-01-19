package com.hwm.hwmonlinebookshop

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_profile)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser

        val profileName = findViewById<TextView>(R.id.profileName)
        val profileEmail = findViewById<TextView>(R.id.profileEmail)
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        // Fetch profile information from Firestore
        currentUser?.uid?.let { uid ->
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name") ?: "Name not available"
                        val email = document.getString("email") ?: "Email not available"
                        profileName.text = name
                        profileEmail.text = email
                    } else {
                        profileName.text = "Name not available"
                        profileEmail.text = "Email not available"
                    }
                }
                .addOnFailureListener { exception ->
                    profileName.text = "Name not available"
                    profileEmail.text = "Email not available"
                }
        }

        // Handle logout
        logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginScreen::class.java))
            finish()
        }
    }
}