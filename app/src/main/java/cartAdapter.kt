package com.hwm.hwmonlinebookshop

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartAdapter(private var cartItems: List<CartItem>) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val bookNameTextView: TextView = view.findViewById(R.id.cartItemName)
        private val bookPriceTextView: TextView = view.findViewById(R.id.cartItemPrice)
        private val bookImageView: ImageView = view.findViewById(R.id.cartItemImage)
        private val bookAuthorTextView: TextView = view.findViewById(R.id.cartItemAuthor)
        private val bookQuantityTextView: TextView = view.findViewById(R.id.cartItemQuantity)

        fun bind(cartItem: CartItem) {
            Log.d("CartAdapter", "Binding cart item: $cartItem") // Added log
            bookNameTextView.text = cartItem.name
            bookPriceTextView.text = "Price: ${cartItem.price}"
            bookAuthorTextView.text = "Author: ${cartItem.author}"
            bookQuantityTextView.text = "Quantity: ${cartItem.quantity}"
            Glide.with(bookImageView.context).load(cartItem.imageUrl).into(bookImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart_product, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount() = cartItems.size

    fun updateCartItems(newCartItems: List<CartItem>) {
        cartItems = newCartItems
        notifyDataSetChanged()
    }
}