package com.hwm.hwmonlinebookshop

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.android.identity.util.UUID
import com.google.firebase.firestore.FirebaseFirestore

class AddBooksData : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_books_data)

        firestore = FirebaseFirestore.getInstance()

        val bookName = findViewById<EditText>(R.id.editTextBookName)
        val bookDescription = findViewById<EditText>(R.id.editTextBookDescription)
        val bookAuthor = findViewById<EditText>(R.id.editTextBookAuthor)
        val bookPrice = findViewById<EditText>(R.id.editTextBookPrice)
        val addButton = findViewById<Button>(R.id.btnAddBook)

        addButton.setOnClickListener {
            val name = bookName.text.toString()
            val description = bookDescription.text.toString()
            val author = bookAuthor.text.toString()
            val price = bookPrice.text.toString()

            if (name.isNotEmpty() && description.isNotEmpty() && author.isNotEmpty() && price.isNotEmpty()) {
                val book = Book(UUID.randomUUID().toString(), name, description, author, price, "https://example.com/book.jpg")

                firestore.collection("books").document(book.id).set(book)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Successfully added $name", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Failed to add $name: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}