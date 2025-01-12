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

class LoginScreen : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_activity)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val emailEditText = findViewById<EditText>(R.id.etEmail)
        val passwordEditText = findViewById<EditText>(R.id.etPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val createAccountButton = findViewById<Button>(R.id.btnNewAccount)
        val forgotPasswordButton = findViewById<Button>(R.id.btnForgotPassword)
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

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showToast("Please enter all fields")
            } else {
                loginUser(email, password)
            }
        }

        createAccountButton.setOnClickListener {
            startActivity(Intent(this, CreateAccount::class.java))
            finish()
        }

        forgotPasswordButton.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    currentUser?.let {
                        navigateToDashboard(it.uid)
                    }
                } else {
                    showToast("Login Failed: ${task.exception?.message}")
                }
            }
    }

    private fun navigateToDashboard(userId: String) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role") ?: "user"
                    val intent = if (role == "admin") {
                        Intent(this, AdminDashboard::class.java)
                    } else {
                        Intent(this, UserDashboard::class.java)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    showToast("User data not found")
                }
            }
            .addOnFailureListener {
                showToast("Failed to retrieve user role")
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}