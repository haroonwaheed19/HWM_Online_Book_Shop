package com.hwm.hwmonlinebookshop

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginScreen : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        val emailEditText = findViewById<EditText>(R.id.etEmail)
        val passwordEditText = findViewById<EditText>(R.id.etPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val createAccountButton = findViewById<Button>(R.id.btnNewAccount)
        val forgotPasswordButton = findViewById<Button>(R.id.btnForgotPassword)

        // Login Button Click Listener
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentUser = auth.currentUser
                        val userId = currentUser?.uid

                        // Fetch role from database
                        val databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)
                        databaseReference.child("role").get()
                            .addOnSuccessListener { snapshot ->
                                val role = snapshot.value.toString()
                                if (role == "admin") {
                                    // Navigate to Admin Dashboard
                                    startActivity(Intent(this, UserDashBoard::class.java))
                                } else {
                                    // Navigate to User Dashboard
                                    startActivity(Intent(this, AdminDashboard::class.java))
                                }
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to retrieve user role", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }

        }

        // Create Account Button Click Listener
        createAccountButton.setOnClickListener {
            startActivity(Intent(this, CreateAccount::class.java))
            finish()
        }

        // Forgot Password Button Click Listener
        forgotPasswordButton.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

    }
}