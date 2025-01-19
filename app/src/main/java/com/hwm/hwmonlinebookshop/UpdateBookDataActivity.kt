package com.hwm.hwmonlinebookshop

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
import com.google.firebase.firestore.FirebaseFirestore

class UpdateBookDataActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var bookId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_book_data)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        bookId = intent.getStringExtra("BOOK_ID") ?: ""
        if (bookId.isEmpty()) {
            Toast.makeText(this, "Book ID is missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val bookName = findViewById<EditText>(R.id.editTextBookName)
        val bookDescription = findViewById<EditText>(R.id.editTextBookDescription)
        val bookAuthor = findViewById<EditText>(R.id.editTextBookAuthor)
        val bookPrice = findViewById<EditText>(R.id.editTextBookPrice)
        val imageView = findViewById<EditText>(R.id.editTextImageLink)
        val updateButton = findViewById<Button>(R.id.btnUpdateBook)

        val currentUser = auth.currentUser
        currentUser?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val idToken = task.result?.token
                val claims = task.result?.claims
                val isAdmin = claims?.get("admin") as? Boolean ?: false
                Log.d("UpdateBookDataActivity", "ID Token: $idToken")
                Log.d("UpdateBookDataActivity", "Claims: $claims")
                Log.d("UpdateBookDataActivity", "Is Admin: $isAdmin")

                if (isAdmin) {
                    // Fetch current book data and populate fields
                    firestore.collection("Books").document(bookId).get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                val book = document.toObject(Book::class.java)
                                bookName.setText(book?.name)
                                bookDescription.setText(book?.description)
                                bookAuthor.setText(book?.author)
                                bookPrice.setText(book?.price)
                                imageView.setText(book?.imageUrl)
                            } else {
                                Toast.makeText(this, "Book not found", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("UpdateBookDataActivity", "Error fetching book data: ${exception.message}")
                            Toast.makeText(this, "Error fetching book data: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "You do not have the required permissions", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                Log.d("UpdateBookDataActivity", "Failed to get ID Token: ${task.exception?.message}")
            }
        }

        // Handle book update
        updateButton.setOnClickListener {
            val name = bookName.text.toString()
            val description = bookDescription.text.toString()
            val author = bookAuthor.text.toString()
            val price = bookPrice.text.toString()
            val imageurl = imageView.text.toString()

            if (name.isNotEmpty() && description.isNotEmpty() && author.isNotEmpty() && price.isNotEmpty() && imageurl.isNotEmpty()) {
                val updatedBook = mapOf(
                    "name" to name,
                    "description" to description,
                    "author" to author,
                    "price" to price,
                    "imageurl" to imageurl
                )

                firestore.collection("Books").document(bookId).update(updatedBook)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Book updated successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        Log.d("UpdateBookDataActivity", "Error updating book: ${exception.message}")
                        Toast.makeText(this, "Error updating book: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}