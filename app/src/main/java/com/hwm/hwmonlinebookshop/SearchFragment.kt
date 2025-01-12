package com.hwm.hwmonlinebookshop

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class SearchFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var bookAdapter: BookAdapter
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        firestore = FirebaseFirestore.getInstance()
        searchRecyclerView = view.findViewById(R.id.searchRecyclerView)
        searchRecyclerView.layoutManager = LinearLayoutManager(context)
        searchView = view.findViewById(R.id.searchView)

        bookAdapter = BookAdapter(emptyList()) { book ->
            // Handle book click, open details
            val intent = Intent(activity, BookDetailsActivity::class.java)
            intent.putExtra("BOOK_ID", book.id)
            startActivity(intent)
        }
        searchRecyclerView.adapter = bookAdapter

        // Fetch books from Firestore
        firestore.collection("Books")
            .get()
            .addOnSuccessListener { result ->
                val books = result.mapNotNull { it.toObject<Book>() }
                bookAdapter.updateBooks(books)
            }
            .addOnFailureListener { exception ->
                // Handle the error
                Toast.makeText(context, "Failed to fetch books: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        // Setup search functionality
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    performSearch(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Optionally handle real-time search
                return false
            }
        })

        return view
    }

    private fun performSearch(query: String) {
        firestore.collection("Books") // Changed to "Books" to match the collection name
            .whereEqualTo("name", query)
            .get()
            .addOnSuccessListener { result ->
                val books = result.mapNotNull { it.toObject<Book>() }
                bookAdapter.updateBooks(books)
            }
            .addOnFailureListener { exception ->
                // Handle the error
                Toast.makeText(context, "Search failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}