package com.hwm.hwmonlinebookshop

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var homeRecyclerView: RecyclerView
    private lateinit var bookAdapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        firestore = FirebaseFirestore.getInstance()
        homeRecyclerView = view.findViewById(R.id.homeRecyclerView)
        homeRecyclerView.layoutManager = LinearLayoutManager(context)

        bookAdapter = BookAdapter(listOf()) { book ->
            // Intent to open BookDetailsActivity
            val intent = Intent(activity, BookDetailsActivity::class.java).apply {
                putExtra("BOOK_ID", book.id)
            }
            startActivity(intent)
        }
        homeRecyclerView.adapter = bookAdapter

        fetchBooks()

        return view
    }

    private fun fetchBooks() {
        firestore.collection("Books")
            .get()
            .addOnSuccessListener { result ->
                val books = result.documents.mapNotNull { doc ->
                    val book = doc.toObject(Book::class.java)?.apply {
                        id = doc.id  // Assign Firestore document ID to the book object
                    }
                    book
                }
                if (books.isNotEmpty()) {
                    bookAdapter.updateBooks(books)
                } else {
                    Toast.makeText(context, "No books found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to fetch books: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}