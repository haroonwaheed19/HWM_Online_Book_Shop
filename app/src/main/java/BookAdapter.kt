package com.hwm.hwmonlinebookshop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BookAdapter(private var books: List<Book>, private val onBookClick: (Book) -> Unit) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    inner class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameTextView: TextView = view.findViewById(R.id.productName)
        private val descriptionTextView: TextView = view.findViewById(R.id.productDescription)
        private val authorTextView: TextView = view.findViewById(R.id.productAuthor)
        private val priceTextView: TextView = view.findViewById(R.id.productPrice)
        private val productImageView: ImageView = view.findViewById(R.id.productImage)

        fun bind(book: Book) {
            nameTextView.text = book.name
            descriptionTextView.text = book.description
            authorTextView.text = book.author
            priceTextView.text = book.price
            Glide.with(productImageView.context).load(book.imageUrl).into(productImageView)

            // Handle book click
            itemView.setOnClickListener {
                onBookClick(book)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount() = books.size

    fun updateBooks(newBooks: List<Book>) {
        books = newBooks
        notifyDataSetChanged()
    }
}