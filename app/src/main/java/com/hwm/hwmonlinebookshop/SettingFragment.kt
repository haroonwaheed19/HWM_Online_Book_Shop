package com.hwm.hwmonlinebookshop

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        // Find buttons by their IDs
        val changePasswordButton = view.findViewById<Button>(R.id.changePasswordButton)
        val updateProfileButton = view.findViewById<Button>(R.id.updateProfileButton)

        // Set click listeners for each button
        changePasswordButton.setOnClickListener {
            navigateToChangePassword()
        }

        updateProfileButton.setOnClickListener {
            navigateToUpdateProfile()
        }

        return view
    }

    private fun navigateToChangePassword() {
        // Navigate to the Change Password Activity
        val intent = Intent(context, ChangePasswordActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToUpdateProfile() {
        // Navigate to the Update Profile Activity
        val intent = Intent(context, UpdateProfileActivity::class.java)
        startActivity(intent)
    }
}