package com.hwm.hwmonlinebookshop

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
import com.google.firebase.firestore.ktx.toObject

class HomeFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var homeRecyclerView: RecyclerView
    private lateinit var bookAdapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("HomeFragment", "onCreateView called")
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        firestore = FirebaseFirestore.getInstance()
        homeRecyclerView = view.findViewById(R.id.homeRecyclerView)
        homeRecyclerView.layoutManager = LinearLayoutManager(context)

        bookAdapter = BookAdapter(listOf()) { book ->
            // Handle book click if needed
            Log.d("HomeFragment", "Book clicked: ${book.name}")
        }
        homeRecyclerView.adapter = bookAdapter

        fetchBooks()

        return view
    }

    private fun fetchBooks() {
        Log.d("HomeFragment", "Fetching books from Firestore")
        firestore.collection("Books")
            .get()
            .addOnSuccessListener { result ->
                Log.d("HomeFragment", "Fetch successful")
                val books = result.mapNotNull {
                    Log.d("HomeFragment", "Book data: ${it.data}")
                    it.toObject<Book>()
                }
                bookAdapter.updateBooks(books)
                if (books.isEmpty()) {
                    Log.d("HomeFragment", "No books found")
                    Toast.makeText(context, "No books found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                // Handle the error
                Log.e("HomeFragment", "Error fetching books", exception)
                Toast.makeText(context, "Failed to fetch books: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}