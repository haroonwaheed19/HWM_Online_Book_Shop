package com.hwm.hwmonlinebookshop

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_change_password)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val newPasswordEditText = findViewById<EditText>(R.id.newPasswordEditText)
        val confirmPasswordEditText = findViewById<EditText>(R.id.confirmPasswordEditText)
        val currentPasswordEditText = findViewById<EditText>(R.id.currentPasswordEditText)
        val changePasswordButton = findViewById<Button>(R.id.changePasswordButton)
        val showHidePasswordImageViewCurrent = findViewById<ImageView>(R.id.ivShowHidePasswordCurrent)
        val showHidePasswordImageViewNew = findViewById<ImageView>(R.id.ivShowHidePasswordNew)
        val showHidePasswordImageViewConfirm = findViewById<ImageView>(R.id.ivShowHidePasswordConfirm)

        // Variables to track visibility state for each password field
        var isCurrentPasswordVisible = false
        var isNewPasswordVisible = false
        var isConfirmPasswordVisible = false

        // Toggle current password visibility
        showHidePasswordImageViewCurrent.setOnClickListener {
            if (isCurrentPasswordVisible) {
                // Hide the password
                currentPasswordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                showHidePasswordImageViewCurrent.setImageResource(R.drawable.ic_eye)
            } else {
                // Show the password
                currentPasswordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                showHidePasswordImageViewCurrent.setImageResource(R.drawable.ic_eye)
            }
            isCurrentPasswordVisible = !isCurrentPasswordVisible

            // Move the cursor to the end of the text
            currentPasswordEditText.setSelection(currentPasswordEditText.text.length)
        }

        // Toggle new password visibility
        showHidePasswordImageViewNew.setOnClickListener {
            if (isNewPasswordVisible) {
                // Hide the password
                newPasswordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                showHidePasswordImageViewNew.setImageResource(R.drawable.ic_eye)
            } else {
                // Show the password
                newPasswordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                showHidePasswordImageViewNew.setImageResource(R.drawable.ic_eye)
            }
            isNewPasswordVisible = !isNewPasswordVisible

            // Move the cursor to the end of the text
            newPasswordEditText.setSelection(newPasswordEditText.text.length)
        }

        // Toggle confirm password visibility
        showHidePasswordImageViewConfirm.setOnClickListener {
            if (isConfirmPasswordVisible) {
                // Hide the password
                confirmPasswordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                showHidePasswordImageViewConfirm.setImageResource(R.drawable.ic_eye)
            } else {
                // Show the password
                confirmPasswordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                showHidePasswordImageViewConfirm.setImageResource(R.drawable.ic_eye)
            }
            isConfirmPasswordVisible = !isConfirmPasswordVisible

            // Move the cursor to the end of the text
            confirmPasswordEditText.setSelection(confirmPasswordEditText.text.length)
        }

        // Change password logic
        changePasswordButton.setOnClickListener {
            val newPassword = newPasswordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()
            val currentPassword = currentPasswordEditText.text.toString()

            if (newPassword.isEmpty() || confirmPassword.isEmpty() || currentPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = auth.currentUser
            val email = user?.email

            if (user != null && email != null) {
                // Re-authenticate user to allow password change
                val credential = EmailAuthProvider.getCredential(email, currentPassword)

                user.reauthenticate(credential).addOnCompleteListener { reAuthTask ->
                    if (reAuthTask.isSuccessful) {
                        user.updatePassword(newPassword)
                            .addOnSuccessListener {
                                // Update the password field in Firestore
                                val userRef = db.collection("users").document(user.uid)
                                userRef.update("password", newPassword)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
                                        finish()
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(this, "Error updating password in Firestore: ${exception.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Re-authentication failed: ${reAuthTask.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show()
            }
        }
    }
}