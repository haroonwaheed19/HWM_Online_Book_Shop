package com.hwm.hwmonlinebookshop

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateAccount : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_account)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val nameEditText = findViewById<EditText>(R.id.etName)
        val emailEditText = findViewById<EditText>(R.id.etEmail)
        val passwordEditText = findViewById<EditText>(R.id.etPassword)
        val createAccountButton = findViewById<Button>(R.id.btnLogin)
        val showHidePasswordImageView = findViewById<ImageView>(R.id.ivShowHidePassword)
        var isPasswordVisible = false

        // Toggle password visibility
        showHidePasswordImageView.setOnClickListener {
            if (isPasswordVisible) {
                // Hide the password
                passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                showHidePasswordImageView.setImageResource(R.drawable.ic_eye)
            } else {
                // Show the password
                passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                showHidePasswordImageView.setImageResource(R.drawable.ic_eye)
            }
            isPasswordVisible = !isPasswordVisible

            // Move the cursor to the end of the text
            passwordEditText.setSelection(passwordEditText.text.length)
        }

        createAccountButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val name = nameEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        if (userId == null) {
                            Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show()
                            return@addOnCompleteListener
                        }

                        val user = hashMapOf(
                            "name" to name,
                            "email" to email,
                            "role" to "user", // Default role is "user"
                            "password" to password
                        )

                        // Log user data before saving
                        println("DEBUG: User data to save: $user")

                        // Save user data in Firestore
                        firestore.collection("users").document(userId).set(user)
                            .addOnSuccessListener {
                                // Log success
                                println("DEBUG: User data successfully saved.")
                                Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, LoginScreen::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                // Log failure
                                println("DEBUG: Failed to save user data: ${e.message}")
                                Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        // Log error for account creation
                        println("DEBUG: Account creation failed: ${task.exception?.message}")
                        Toast.makeText(this, "Account Creation Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}