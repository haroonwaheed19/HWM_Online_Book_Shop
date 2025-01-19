package com.hwm.hwmonlinebookshop

import android.content.Intent
import android.os.Bundle
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

        // Get book details passed through Intent
        val bookId = intent.getStringExtra("BOOK_ID")
        val bookName = intent.getStringExtra("BOOK_NAME")
        val bookAuthor = intent.getStringExtra("BOOK_AUTHOR")
        val bookDescription = intent.getStringExtra("BOOK_DESCRIPTION")
        val bookPrice = intent.getStringExtra("BOOK_PRICE")
        val bookImageUrl = intent.getStringExtra("BOOK_IMAGE_URL")

        // Set the book details in the UI
        bookNameTextView.text = "Book Name\n\n$bookName"
        bookAuthorTextView.text = "Book Author\n\n$bookAuthor"
        bookDescriptionTextView.text = "Book Description\n\n$bookDescription"
        bookPriceTextView.text = "Book Price\n\n$bookPrice"
        Glide.with(this).load(bookImageUrl).into(bookImageView)

        // Handle the purchase confirmation
        confirmPurchaseButton.setOnClickListener {
            confirmPurchase(bookId, bookName, bookPrice)
        }
    }

    private fun confirmPurchase(bookId: String?, bookName: String?, bookPrice: String?) {
        val currentUser = auth.currentUser

        if (currentUser == null || bookId.isNullOrEmpty() || bookName.isNullOrEmpty() || bookPrice.isNullOrEmpty()) {
            Toast.makeText(this, "Invalid purchase details. Please try again.", Toast.LENGTH_SHORT).show()
            return
        }

        val purchase = hashMapOf(
            "bookId" to bookId,
            "bookName" to bookName,
            "userId" to currentUser.uid,
            "price" to bookPrice,
            "purchaseDate" to System.currentTimeMillis()
        )

        firestore.collection("purchases")
            .add(purchase)
            .addOnSuccessListener {
                Toast.makeText(this, "Purchase confirmed! Thank you!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, UserDashboard::class.java)
                clearPurchasedItemFromCart(currentUser.uid, bookId)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Purchase failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun clearPurchasedItemFromCart(userId: String, bookId: String?) {
        if (bookId == null) return

        // Access the user's cart and remove the specific item
        val cartRef = firestore.collection("carts").document(userId)
            .collection("items").document(bookId)

        cartRef.delete()
            .addOnSuccessListener {
                Toast.makeText(this, "The purchased item has been removed from the cart.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to remove the item from the cart: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

}