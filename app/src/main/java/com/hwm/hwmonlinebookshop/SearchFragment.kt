//package com.hwm.hwmonlinebookshop
//
//import android.content.Intent
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ProgressBar
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import androidx.appcompat.widget.SearchView // <-- Ensure this is the correct import
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.ktx.toObject
//
//class SearchFragment : Fragment() {
//
//    private lateinit var firestore: FirebaseFirestore
//    private lateinit var searchRecyclerView: RecyclerView
//    private lateinit var bookAdapter: BookAdapter
//    private lateinit var searchView: SearchView
//    private lateinit var loadingIndicator: ProgressBar
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_search, container, false)
//
//        firestore = FirebaseFirestore.getInstance()
//        searchRecyclerView = view.findViewById(R.id.searchRecyclerView)
//        searchRecyclerView.layoutManager = LinearLayoutManager(context)
//        searchView = view.findViewById(R.id.searchView)
//        loadingIndicator = view.findViewById(R.id.loadingIndicator)
//
//        bookAdapter = BookAdapter(emptyList()) { book ->
//            book.id?.let {
//                val intent = Intent(activity, BookDetailsActivity::class.java)
//                intent.putExtra("BOOK_ID", it)
//                startActivity(intent)
//            }
//        }
//        searchRecyclerView.adapter = bookAdapter
//
//        fetchBooks()
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                query?.let {
//                    performSearch(it)
//                }
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                if (newText.isNullOrEmpty()) {
//                    fetchBooks()
//                } else {
//                    performSearch(newText)
//                }
//                return false
//            }
//        })
//
//        return view
//    }
//
//    private fun fetchBooks() {
//        loadingIndicator.visibility = View.VISIBLE
//        firestore.collection("Books")
//            .get()
//            .addOnSuccessListener { result ->
//                loadingIndicator.visibility = View.GONE
//                val books = result.mapNotNull { it.toObject<Book>() }
//                bookAdapter.updateBooks(books)
//            }
//            .addOnFailureListener { exception ->
//                loadingIndicator.visibility = View.GONE
//                Toast.makeText(context, "Failed to fetch books: ${exception.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun performSearch(query: String) {
//        loadingIndicator.visibility = View.VISIBLE
//        firestore.collection("Books")
//            .whereGreaterThanOrEqualTo("name", query)
//            .whereLessThanOrEqualTo("name", query + "\uf8ff") // For prefix search
//            .get()
//            .addOnSuccessListener { result ->
//                loadingIndicator.visibility = View.GONE
//                val books = result.mapNotNull { it.toObject<Book>() }
//                if (books.isEmpty()) {
//                    Toast.makeText(context, "No results found", Toast.LENGTH_SHORT).show()
//                }
//                bookAdapter.updateBooks(books)
//            }
//            .addOnFailureListener { exception ->
//                loadingIndicator.visibility = View.GONE
//                Toast.makeText(context, "Search failed: ${exception.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//}


package com.hwm.hwmonlinebookshop

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
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
    private lateinit var loadingIndicator: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        firestore = FirebaseFirestore.getInstance()
        searchRecyclerView = view.findViewById(R.id.searchRecyclerView)
        searchRecyclerView.layoutManager = LinearLayoutManager(context)
        searchView = view.findViewById(R.id.searchView)
        loadingIndicator = view.findViewById(R.id.loadingIndicator)

        bookAdapter = BookAdapter(emptyList()) { book ->
            book.id?.let {
                val intent = Intent(activity, BookDetailsActivity::class.java)
                intent.putExtra("BOOK_ID", it)
                startActivity(intent)
            }
        }
        searchRecyclerView.adapter = bookAdapter

        fetchBooks()

        // Make the entire search view clickable
        searchView.isFocusable = true
        searchView.isFocusableInTouchMode = true
        searchView.setIconifiedByDefault(false) // Ensure the search view is expanded

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    performSearch(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    fetchBooks()
                } else {
                    performSearch(newText)
                }
                return false
            }
        })

        searchView.setOnClickListener {
            searchView.requestFocus()
        }

        return view
    }

    private fun fetchBooks() {
        loadingIndicator.visibility = View.VISIBLE
        firestore.collection("Books")
            .get()
            .addOnSuccessListener { result ->
                loadingIndicator.visibility = View.GONE
                val books = result.mapNotNull { it.toObject<Book>() }
                bookAdapter.updateBooks(books)
            }
            .addOnFailureListener { exception ->
                loadingIndicator.visibility = View.GONE
                Toast.makeText(context, "Failed to fetch books: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

        private fun performSearch(query: String) {
        loadingIndicator.visibility = View.VISIBLE
        firestore.collection("Books")
            .whereGreaterThanOrEqualTo("name", query)
            .whereLessThanOrEqualTo("name", query + "\uf8ff") // For prefix search
            .get()
            .addOnSuccessListener { result ->
                loadingIndicator.visibility = View.GONE
                val books = result.mapNotNull { it.toObject<Book>() }
                if (books.isEmpty()) {
                    Toast.makeText(context, "No results found", Toast.LENGTH_SHORT).show()
                }
                bookAdapter.updateBooks(books)
            }
            .addOnFailureListener { exception ->
                loadingIndicator.visibility = View.GONE
                Toast.makeText(context, "Search failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}