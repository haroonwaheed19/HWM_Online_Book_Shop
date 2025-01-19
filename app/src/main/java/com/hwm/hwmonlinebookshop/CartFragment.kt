package com.hwm.hwmonlinebookshop

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CartFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var cartEmptyTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView)
        cartEmptyTextView = view.findViewById(R.id.cartEmptyTextView)

        // Set RecyclerView layout and adapter
        cartRecyclerView.layoutManager = LinearLayoutManager(context)
        cartAdapter = CartAdapter(listOf(), ::removeFromCart, ::onCartItemClick)
        cartRecyclerView.adapter = cartAdapter

        // Fetch cart items
        fetchCartItems()

        return view
    }

    private fun fetchCartItems() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            firestore.collection("carts").document(user.uid)
                .collection("items")
                .get()
                .addOnSuccessListener { result ->
                    val cartItems = result.documents.mapNotNull { it.toObject(CartItem::class.java) }
                    if (cartItems.isNotEmpty()) {
                        cartAdapter.updateCartItems(cartItems)
                        cartRecyclerView.visibility = View.VISIBLE
                        cartEmptyTextView.visibility = View.GONE
                    } else {
                        cartRecyclerView.visibility = View.GONE
                        cartEmptyTextView.visibility = View.VISIBLE
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Failed to fetch cart items: ${exception.message}", Toast.LENGTH_SHORT).show()
                    cartRecyclerView.visibility = View.GONE
                    cartEmptyTextView.visibility = View.VISIBLE
                }
        } ?: run {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            cartRecyclerView.visibility = View.GONE
            cartEmptyTextView.visibility = View.VISIBLE
        }
    }

    private fun removeFromCart(cartItem: CartItem) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val cartRef = firestore.collection("carts").document(user.uid)
                .collection("items").document(cartItem.id)

            cartRef.delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "${cartItem.name} removed from cart", Toast.LENGTH_SHORT).show()
                    fetchCartItems()  // Refresh the cart items
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Failed to remove from cart: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onCartItemClick(it: CartItem) {
        // Navigate to CheckoutActivity and pass the selected cart item details
        val intent = Intent(context, CheckoutActivity::class.java).apply {
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