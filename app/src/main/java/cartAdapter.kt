package com.hwm.hwmonlinebookshop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartAdapter(
    private var cartItems: List<CartItem>,
    private val onRemoveClickListener: (CartItem) -> Unit,
    private val onItemClickListener: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart_product, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.bind(cartItem)
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateCartItems(newCartItems: List<CartItem>) {
        cartItems = newCartItems
        notifyDataSetChanged()
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemName: TextView = itemView.findViewById(R.id.cartItemName)
        private val itemQuantity: TextView = itemView.findViewById(R.id.cartItemQuantity)
        private val itemPrice: TextView = itemView.findViewById(R.id.cartItemPrice)
        private val itemImage: ImageView = itemView.findViewById(R.id.cartItemImage)
        private val removeButton: Button = itemView.findViewById(R.id.removeButton)

        fun bind(cartItem: CartItem) {
            itemName.text = cartItem.name
            itemQuantity.text = "Quantity: ${cartItem.quantity}"
            itemPrice.text = "Price: ${cartItem.price}"
            Glide.with(itemView).load(cartItem.imageUrl).into(itemImage)

            // Handle item click
            itemView.setOnClickListener {
                onItemClickListener(cartItem)
            }

            // Handle remove button click
            removeButton.setOnClickListener {
                onRemoveClickListener(cartItem)
            }
        }
    }
}