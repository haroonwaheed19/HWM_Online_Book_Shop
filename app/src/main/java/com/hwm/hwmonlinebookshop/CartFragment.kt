package com.hwm.hwmonlinebookshop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class CartFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var cartEmptyTextView: TextView
    private lateinit var checkoutButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        firestore = FirebaseFirestore.getInstance()
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView)
        cartEmptyTextView = view.findViewById(R.id.cartEmptyTextView)
        checkoutButton = view.findViewById(R.id.checkoutButton)

        cartRecyclerView.layoutManager = LinearLayoutManager(context)

        cartAdapter = CartAdapter(emptyList())
        cartRecyclerView.adapter = cartAdapter

        // Fetch cart items from Firestore
        firestore.collection("cart")
            .get()
            .addOnSuccessListener { result ->
                val items = result.mapNotNull { it.toObject<CartItem>() }
                cartAdapter.updateCart(items)
                cartEmptyTextView.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
                checkoutButton.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }

        return view
    }
}