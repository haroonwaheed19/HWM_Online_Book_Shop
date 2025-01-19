package com.hwm.hwmonlinebookshop

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class BookListActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookAdapter: BookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("BookListActivity", "onCreate: Starting BookListActivity")
        enableEdgeToEdge()
        setContentView(R.layout.activity_book_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firestore = FirebaseFirestore.getInstance()
        recyclerView = findViewById(R.id.recyclerViewBooks)
        recyclerView.layoutManager = LinearLayoutManager(this)

        bookAdapter = BookAdapter(emptyList()) { book ->
            Log.d("BookListActivity", "Book clicked: ${book?.id}")
            val intent = Intent(this, UpdateBookDataActivity::class.java).apply {
                putExtra("BOOK_ID", book?.id ?: "")
            }
            startActivity(intent)
        }
        recyclerView.adapter = bookAdapter

        loadBooks()
    }

    private fun loadBooks() {
        Log.d("BookListActivity", "loadBooks: Fetching books from Firestore")
        firestore.collection("Books")
            .get()
            .addOnSuccessListener { result ->
                val books = result.documents.mapNotNull { doc ->
                    val book = doc.toObject(Book::class.java)?.apply {
                        id = doc.id
                    }
                    Log.d("BookListActivity", "Fetched book: $book")
                    book
                }
                if (books.isNotEmpty()) {
                    bookAdapter.updateBooks(books)
                    Log.d("BookListActivity", "Books list updated: $books")
                } else {
                    Toast.makeText(this, "No books found", Toast.LENGTH_SHORT).show()
                    Log.d("BookListActivity", "No books found")
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Failed to fetch books: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("BookListActivity", "Failed to fetch books: ${exception.message}")
            }
    }
}