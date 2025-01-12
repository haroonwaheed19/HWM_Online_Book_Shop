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
            fetchBookDetails(bookId)
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

    private fun fetchBookDetails(bookId: String) {
        firestore.collection("Books").document(bookId).get()
            .addOnSuccessListener { document ->
                book = document.toObject(Book::class.java)
                if (book != null) {
                    bookNameTextView.text = book?.name
                    bookAuthorTextView.text = book?.author
                    bookDescriptionTextView.text = book?.description
                    bookPriceTextView.text = book?.price.toString()
                    Glide.with(this).load(book?.imageUrl).into(bookImageView)
                    Log.d("BookDetailsActivity", "Fetched book details: $book") // Log fetched book details
                } else {
                    Toast.makeText(this, "Book not found", Toast.LENGTH_SHORT).show()
                    Log.e("BookDetailsActivity", "Book not found")
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching book details: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("BookDetailsActivity", "Error fetching book details", exception)
            }
    }

    private fun addToCart() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to add items to your cart.", Toast.LENGTH_SHORT).show()
            return
        }

        book?.let {
            if (it.id.isNullOrEmpty()) {
                Toast.makeText(this, "Invalid book ID.", Toast.LENGTH_SHORT).show()
                return
            }

            val cartItem = hashMapOf(
                "id" to it.id,
                "name" to it.name,
                "description" to it.description,
                "author" to it.author,
                "price" to it.price,
                "quantity" to 1,
                "imageUrl" to it.imageUrl
            )

            firestore.collection("carts").document(currentUser.uid)
                .collection("items")
                .document(it.id)
                .set(cartItem)
                .addOnSuccessListener {
                    Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show()
                    Log.d("BookDetailsActivity", "Cart item added: $cartItem") // Log cart item
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error adding to cart: ${exception.message}", Toast.LENGTH_SHORT).show()
                    Log.e("BookDetailsActivity", "Error adding to cart", exception)
                }
        } ?: run {
            Toast.makeText(this, "Book details are null.", Toast.LENGTH_SHORT).show()
        }
    }
}