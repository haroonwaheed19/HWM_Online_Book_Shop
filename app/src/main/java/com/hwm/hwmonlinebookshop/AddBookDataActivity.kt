package com.hwm.hwmonlinebookshop

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AddBooksData : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_books_data)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val bookName = findViewById<EditText>(R.id.editTextBookName)
        val bookDescription = findViewById<EditText>(R.id.editTextBookDescription)
        val bookAuthor = findViewById<EditText>(R.id.editTextBookAuthor)
        val bookPrice = findViewById<EditText>(R.id.editTextBookPrice)
        val addButton = findViewById<Button>(R.id.btnAddBook)
        val imageView = findViewById<EditText>(R.id.editTextImageLink)

        addButton.setOnClickListener {
            val name = bookName.text.toString()
            val description = bookDescription.text.toString()
            val author = bookAuthor.text.toString()
            val price = bookPrice.text.toString()
            val imageurl = imageView.text.toString()

            val currentUser = auth.currentUser
            if (currentUser == null) {
                Toast.makeText(this, "You must be signed in to add a book", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            currentUser.getIdToken(true).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val tokenResult: GetTokenResult = task.result
                    val isAdmin = tokenResult.claims["admin"] as? Boolean ?: false

                    Log.d("AddBooksData", "Admin status: $isAdmin")

                    if (!isAdmin) {
                        Toast.makeText(this, "You do not have permission to add books", Toast.LENGTH_SHORT).show()
                        Log.d("AddBooksData", "User is not an admin")
                        return@addOnCompleteListener
                    }

                    if (name.isNotEmpty() && description.isNotEmpty() && author.isNotEmpty() && price.isNotEmpty() && imageurl.isNotEmpty()) {
                        val bookId = UUID.randomUUID().toString()
                        val book = Book(bookId, name, description, author, price, imageurl)

                        firestore.collection("Books").document(book.id).set(book)
                            .addOnSuccessListener {
                                Log.d("AddBooksData", "Successfully added $name")
                                Toast.makeText(this, "Successfully added $name", Toast.LENGTH_SHORT).show()
                                // Clear input fields after successful addition
                                bookName.text.clear()
                                bookDescription.text.clear()
                                bookAuthor.text.clear()
                                bookPrice.text.clear()
                                imageView.text.clear()
                            }
                            .addOnFailureListener { exception ->
                                Log.d("AddBooksData", "Failed to add $name: ${exception.message}")
                                Toast.makeText(this, "Failed to add $name: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("AddBooksData", "Failed to check admin status: ${task.exception?.message}")
                    Toast.makeText(this, "Failed to check admin status: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}