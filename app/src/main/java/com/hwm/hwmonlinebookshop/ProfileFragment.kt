package com.hwm.hwmonlinebookshop

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        profileName = view.findViewById(R.id.profileName)
        profileEmail = view.findViewById(R.id.profileEmail)
        logoutButton = view.findViewById(R.id.logoutButton)

        // Fetch and display user data
        val currentUser = auth.currentUser
        currentUser?.let {
            profileEmail.text = it.email
            firestore.collection("users").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        profileName.text = document.getString("name")
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle the error
                }
        }

        // Logout button functionality
        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(activity, LoginScreen::class.java)
            startActivity(intent)
            activity?.finish()
        }

        return view
    }
}