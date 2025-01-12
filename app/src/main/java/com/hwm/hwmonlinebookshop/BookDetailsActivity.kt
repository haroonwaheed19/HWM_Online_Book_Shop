package com.hwm.hwmonlinebookshop

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BookDetailsActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var bookImageView: ImageView
    private lateinit var bookNameTextView: TextView
    private lateinit var bookAuthorTextView: TextView
    private lateinit var bookDescriptionTextView: TextView
    private lateinit var bookPriceTextView: TextView
    private lateinit var addToCartButton: Button
    private lateinit var buyNowButton: Button
    private var book: Book? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_book_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        bookImageView = findViewById(R.id.bookImage)
        bookNameTextView = findViewById(R.id.bookName)
        bookAuthorTextView = findViewById(R.id.bookAuthor)
        bookDescriptionTextView = findViewById(R.id.bookDescription)
        bookPriceTextView = findViewById(R.id.bookPrice)
        addToCartButton = findViewById(R.id.addToCartButton)
        buyNowButton = findViewById(R.id.buyNowButton)

        val bookId = intent.getStringExtra("BOOK_ID")
        if (bookId != null) {
            firestore.collection("books").document(bookId).get()
                .addOnSuccessListener { document ->
                    book = document.toObject(Book::class.java)
                    book?.let {
                        bookNameTextView.text = it.name
                        bookAuthorTextView.text = it.author
                        bookDescriptionTextView.text = it.description
                        bookPriceTextView.text = it.price.toString()
                        Glide.with(this).load(it.imageUrl).into(bookImageView)
                    } ?: run {
                        Toast.makeText(this, "Book not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error fetching book details: ${exception.message}", Toast.LENGTH_SHORT).show()
                    Log.e("BookDetailsActivity", "Error fetching book details", exception)
                }
        } else {
            Toast.makeText(this, "Book ID is null", Toast.LENGTH_SHORT).show()
        }

        addToCartButton.setOnClickListener {
            addToCart()
        }

        buyNowButton.setOnClickListener {
            book?.let {
                val intent = Intent(this, CheckoutActivity::class.java).apply {
                    putExtra("BOOK_ID", it.id)
                    putExtra("BOOK_NAME", it.name)
                    putExtra("BOOK_AUTHOR", it.author)
                    putExtra("BOOK_DESCRIPTION", it.description)
                    putExtra("BOOK_PRICE", it.price)
                    putExtra("BOOK_IMAGE_URL", it.imageUrl)
                }
                startActivity(intent)
            }
        }
    }

    private fun addToCart() {
        val currentUser = auth.currentUser
        book?.let {
            val cartItem = hashMapOf(
                "id" to it.id,
                "name" to it.name,
                "description" to it.description,
                "author" to it.author,
                "price" to it.price,
                "quantity" to 1,
                "imageUrl" to it.imageUrl
            )
            currentUser?.let { user ->
                firestore.collection("carts").document(user.uid)
                    .collection("items")
                    .document(it.id)
                    .set(cartItem)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Error adding to cart: ${exception.message}", Toast.LENGTH_SHORT).show()
                        Log.e("BookDetailsActivity", "Error adding to cart", exception)
                    }
            } ?: run {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "Book details are null", Toast.LENGTH_SHORT).show()
        }
    }
}