package com.hwm.hwmonlinebookshop

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CheckoutActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var bookImageView: ImageView
    private lateinit var bookNameTextView: TextView
    private lateinit var bookAuthorTextView: TextView
    private lateinit var bookDescriptionTextView: TextView
    private lateinit var bookPriceTextView: TextView
    private lateinit var confirmPurchaseButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        enableEdgeToEdge()

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
        confirmPurchaseButton = findViewById(R.id.confirmPurchaseButton)

        val bookId = intent.getStringExtra("BOOK_ID")
        val bookName = intent.getStringExtra("BOOK_NAME")
        val bookAuthor = intent.getStringExtra("BOOK_AUTHOR")
        val bookDescription = intent.getStringExtra("BOOK_DESCRIPTION")
        val bookPrice = intent.getStringExtra("BOOK_PRICE")
        val bookImageUrl = intent.getStringExtra("BOOK_IMAGE_URL")

        bookNameTextView.text = bookName
        bookAuthorTextView.text = bookAuthor
        bookDescriptionTextView.text = bookDescription
        bookPriceTextView.text = bookPrice
        Glide.with(this).load(bookImageUrl).into(bookImageView)

        confirmPurchaseButton.setOnClickListener {
            confirmPurchase(bookId)
        }
    }

    private fun confirmPurchase(bookId: String?) {
        val currentUser = auth.currentUser
        val purchase = hashMapOf(
            "bookId" to bookId,
            "userId" to currentUser?.uid,
            "purchaseDate" to System.currentTimeMillis()
        )
        currentUser?.let {
            firestore.collection("purchases")
                .add(purchase)
                .addOnSuccessListener {
                    // Handle successful purchase
                }
                .addOnFailureListener { exception ->
                    // Handle the error
                }
        }
    }
}