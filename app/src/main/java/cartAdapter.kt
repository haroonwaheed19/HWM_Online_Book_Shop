package com.hwm.hwmonlinebookshop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartAdapter(private var cartItems: List<CartItem>) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.productName)
        val descriptionTextView: TextView = view.findViewById(R.id.productDescription)
        val authorTextView: TextView = view.findViewById(R.id.productAuthor)
        val priceTextView: TextView = view.findViewById(R.id.productPrice)
        val quantityTextView: TextView = view.findViewById(R.id.productQuantity)
        val productImageView: ImageView = view.findViewById(R.id.productImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart_product, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.nameTextView.text = cartItem.name
        holder.descriptionTextView.text = cartItem.description
        holder.authorTextView.text = cartItem.author
        holder.priceTextView.text = cartItem.price.toString()
        holder.quantityTextView.text = cartItem.quantity.toString()
        Glide.with(holder.productImageView.context).load(cartItem.imageUrl).into(holder.productImageView)
    }

    override fun getItemCount() = cartItems.size

    fun updateCart(items: List<CartItem>) {
        cartItems = items
        notifyDataSetChanged()
    }
}