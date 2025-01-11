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

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val logoutButton = findViewById<Button>(R.id.btnLogout)
        val textView = findViewById<TextView>(R.id.tvEmail)
        if (user == null) {
            startActivity(Intent(this, LoginScreen::class.java))
            finish()
        }
        else {
            textView.text = user.email
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginScreen::class.java))
            finish()
        }

    }
}