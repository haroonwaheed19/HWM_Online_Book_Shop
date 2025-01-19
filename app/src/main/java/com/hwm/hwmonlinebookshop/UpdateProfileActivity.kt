package com.hwm.hwmonlinebookshop

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_profile)

        // Enable edge-to-edge UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Initialize views
        val displayNameEditText = findViewById<EditText>(R.id.displayNameEditText)
        val updateProfileButton = findViewById<Button>(R.id.updateProfileButton)

        // Clear the EditText initially
        displayNameEditText.text.clear()

        // Log the current display name before update
        Log.d("UpdateProfileActivity", "Current Display Name: ${user.displayName}")

        // Handle profile update when button is clicked
        updateProfileButton.setOnClickListener {
            val displayName = displayNameEditText.text.toString()

            // Check if display name is empty
            if (displayName.isEmpty()) {
                Toast.makeText(this, "Display name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create a UserProfileChangeRequest to update the display name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()

            // Update the user's profile in Firebase Authentication
            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("UpdateProfileActivity", "Profile updated in Firebase Auth")

                        // Reload the user profile to reflect the changes
                        auth.currentUser?.reload()?.addOnCompleteListener { reloadTask ->
                            if (reloadTask.isSuccessful) {
                                Log.d("UpdateProfileActivity", "User profile reloaded")

                                // Update the name field in Firestore
                                val userRef = db.collection("users").document(user.uid)
                                userRef.get().addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        userRef.update("name", displayName)
                                            .addOnSuccessListener {
                                                Log.d("UpdateProfileActivity", "Profile name updated in Firestore")

                                                // Navigate back to UserDashboard and refresh the drawer header
                                                val intent = Intent(this, UserDashboard::class.java)
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                                startActivity(intent)

                                                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                                                finish()  // Finish the activity after successful update
                                            }
                                            .addOnFailureListener { exception ->
                                                Log.d("UpdateProfileActivity", "Error updating profile name in Firestore: ${exception.message}")
                                                Toast.makeText(this, "Error updating profile name in Firestore: ${exception.message}", Toast.LENGTH_SHORT).show()
                                            }
                                    } else {
                                        Log.d("UpdateProfileActivity", "Document does not exist")
                                        Toast.makeText(this, "User document does not exist in Firestore", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Log.d("UpdateProfileActivity", "Failed to reload user")
                                Toast.makeText(this, "Failed to reload user", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Log.d("UpdateProfileActivity", "Error updating profile in Firebase Auth: ${task.exception?.message}")
                        Toast.makeText(this, "Error updating profile in Firebase Auth: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}