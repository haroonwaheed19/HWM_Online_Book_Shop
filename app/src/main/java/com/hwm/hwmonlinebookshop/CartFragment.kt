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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CartFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView)
        cartRecyclerView.layoutManager = LinearLayoutManager(context)

        cartAdapter = CartAdapter(listOf())
        cartRecyclerView.adapter = cartAdapter

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
                    Log.d("CartFragment", "Cart items fetched: $cartItems") // Added log for fetched items
                    if (cartItems.isNotEmpty()) {
                        cartAdapter.updateCartItems(cartItems)
                        cartAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(context, "No items in cart", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Failed to fetch cart items: ${exception.message}", Toast.LENGTH_SHORT).show()
                    Log.e("CartFragment", "Error fetching cart items", exception)
                }
        } ?: run {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}