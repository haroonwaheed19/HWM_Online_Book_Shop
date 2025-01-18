package com.hwm.hwmonlinebookshop

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore // Firestore instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Ensure proper padding for edge-to-edge UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize FirebaseAuth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Add sample data to Firestore
//        addSampleDataToFirestore()

        // Delay for splash-like effect
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, 400) // Delay for 0.4 seconds (400 milliseconds)
    }

    private fun navigateToNextScreen() {
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            // User is signed in, navigate to User Dashboard
            Intent(this, UserDashboard::class.java).also {
                startActivity(it)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out) // Smooth transition
                finish()
            }
        } else {
            // No user is signed in, navigate to Login Screen
            Intent(this, LoginScreen::class.java).also {
                startActivity(it)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out) // Smooth transition
                finish()
            }
        }
    }

    private fun addSampleDataToFirestore() {
        // Define a collection for books
        val booksCollection = db.collection("Books")

        // Create sample book data
        // Create sample book data
        val books = listOf(
            Book(
                "1",
                "The Great Gatsby",
                "A classic novel by F. Scott Fitzgerald that explores themes of wealth, love, and the American Dream in the Jazz Age.",
                "F. Scott Fitzgerald",
                "$10.99",
                "https://images.unsplash.com/photo-1512820790803-83ca734da794?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8MXx8Y29tcGxleHRleHxlbnwwfDB8fDE2NjgwNTQ2MjE&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "2",
                "1984",
                "A dystopian novel by George Orwell that delves into the dangers of totalitarianism and extreme political ideology.",
                "George Orwell",
                "$8.99",
                "https://images.unsplash.com/photo-1519681393784-d120267933ba?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8Mnx8MTk4fGVufDB8fDE2NjgwNTQ5Njk&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "3",
                "To Kill a Mockingbird",
                "Harper Lee's Pulitzer Prize-winning novel that examines racism and moral growth in the Deep South during the 1930s.",
                "Harper Lee",
                "$12.50",
                "https://images.unsplash.com/photo-1512026203744-c041fda7e8c8?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8M3x8VG8rS2lsbGluZyxtb2NrZW5pZ3J8ZW58MHx8fDE2NjgwNTU3MzM&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "4",
                "Pride and Prejudice",
                "A romantic comedy by Jane Austen that critiques societal expectations and explores the evolving relationship between Elizabeth Bennet and Mr. Darcy.",
                "Jane Austen",
                "$9.75",
                "https://images.unsplash.com/photo-1508921912186-1d1a45ebb3c1?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8NHx8cHJpZGV8ZW58MHx8fDE2NjgwNTYwMzI&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "5",
                "The Catcher in the Rye",
                "J.D. Salinger's iconic novel about teenage angst, alienation, and self-discovery as narrated by the unforgettable Holden Caulfield.",
                "J.D. Salinger",
                "$11.99",
                "https://images.unsplash.com/photo-1563201517-e7e4f6b1404f?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8NXx8Y2F0Y2hlciUyMmlufDB8fDE2NjgwNTc1OTQ&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "6",
                "The Hobbit",
                "J.R.R. Tolkien's classic fantasy adventure following Bilbo Baggins as he embarks on an epic journey to recover a lost treasure.",
                "J.R.R. Tolkien",
                "$14.50",
                "https://images.unsplash.com/photo-1517836357463-d25dfeac3438?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8NXx8VGhlK0hob2JpdHx3ZW58MHx8fDE2NjgwNTg2MDI&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "7",
                "The Alchemist",
                "Paulo Coelho's inspiring tale of a shepherd boy, Santiago, who pursues his dreams and discovers his true purpose in life.",
                "Paulo Coelho",
                "$10.00",
                "https://images.unsplash.com/photo-1553729459-efe14ef6055d?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8N3x8dGhlJTIwYWxoY2VtaXN0fHxlbnwwfDB8fDE2NjgwNTkzNzA&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "8",
                "The Road",
                "A haunting post-apocalyptic novel by Cormac McCarthy that portrays a father and son's journey for survival in a desolate world.",
                "Cormac McCarthy",
                "$13.25",
                "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8OHx8VGhlJTIwUm9hZHx3ZW58MHx8fDE2NjgwNjAyMTc&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "9",
                "Sapiens: A Brief History of Humankind",
                "Yuval Noah Harari's groundbreaking exploration of the history, evolution, and impact of Homo sapiens on the world.",
                "Yuval Noah Harari",
                "$18.99",
                "https://images.unsplash.com/photo-1579547621706-1a9c79d5bfa7?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8OXx8c2FwaWVucyUyMGhpYWxpZ3h8ZW58MHx8fDE2NjgwNjA5Nzg&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "10",
                "Becoming",
                "Michelle Obama's powerful memoir that chronicles her journey from Chicago's South Side to becoming the First Lady of the United States.",
                "Michelle Obama",
                "$16.75",
                "https://images.unsplash.com/photo-1590608897129-79f6b7bf8a36?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8Mnx8YmVjb21pbmd8fDB8fDE2NjgwNjE3Mzg&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "11",
                "The Lord of the Rings",
                "J.R.R. Tolkien's epic fantasy trilogy that follows the journey of Frodo Baggins to destroy a powerful ring.",
                "J.R.R. Tolkien",
                "$25.99",
                "https://images.unsplash.com/photo-1507837223052-84fba022bc5e?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8M3x8VGhlJTIwTG9yZCUyMG9mJTIwdGhlJTIwUmluZ3xlbnwwfDB8fDE2NjgwNjI3Mzk&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "12",
                "Moby Dick",
                "Herman Melville's classic novel about the obsessive quest of Captain Ahab for revenge against the white whale, Moby Dick.",
                "Herman Melville",
                "$15.00",
                "https://images.unsplash.com/photo-1520004434532-668416a08753?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8Mnx8TW9ieSBEaWNrfHxlbnwwfDB8fDE2NjgwNjM5Mjk&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "13",
                "Crime and Punishment",
                "Fyodor Dostoevsky's psychological masterpiece that explores morality, guilt, and redemption in 19th-century Russia.",
                "Fyodor Dostoevsky",
                "$14.99",
                "https://images.unsplash.com/photo-1518153483313-c5688002b659?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8Mnx8Q3JpbWUuYW5kJFB1bmlzaG1lbnR8ZW58MHx8fDE2NjgwNjQwNjA&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "14",
                "Frankenstein",
                "Mary Shelley's Gothic novel about the consequences of creating life and the relationship between creator and creation.",
                "Mary Shelley",
                "$13.99",
                "https://images.unsplash.com/photo-1563257063-bd303601cdb5?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8MXx8RnJha2Vuc3RlaW5lbmNlfHxlbnwwfDB8fDE2NjgwNjU5OTQ&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "15",
                "Dracula",
                "Bram Stoker's famous horror novel that tells the story of Count Dracula's attempt to move from Transylvania to England in order to spread the undead curse.",
                "Bram Stoker",
                "$12.50",
                "https://images.unsplash.com/photo-1520865678124-bf2e4a869b9e?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8M3x8RHJhdWxhfGVufDB8fDE2NjgwNzAwMzY&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "16",
                "The Hunger Games",
                "Suzanne Collins' dystopian novel where a young girl, Katniss Everdeen, becomes the symbol of revolution in a society controlled by a cruel government.",
                "Suzanne Collins",
                "$9.99",
                "https://images.unsplash.com/photo-1574793389718-e1e8b58a4a4e?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8M3x8VGhlK0h1bmdlciUyMEdhbWVzfHxlbnwwfDB8fDE2NjgwNzAxNjg&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "17",
                "The Chronicles of Narnia",
                "C.S. Lewis' fantasy series that takes readers into the magical world of Narnia, where four children embark on a journey to save the land from an evil queen.",
                "C.S. Lewis",
                "$11.99",
                "https://images.unsplash.com/photo-1562928539-9ccf12a9b4d8?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8MXx8VGhlJTIwQ2hyb25pY2xlc3xlbnwwfDB8fDE2NjgwNzA2NjA&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "18",
                "The Outsiders",
                "S.E. Hinton's coming-of-age novel about a group of teenagers who struggle with class division and societal expectations.",
                "S.E. Hinton",
                "$8.50",
                "https://images.unsplash.com/photo-1520050604101-424d99f7e47b?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8OHx8VGhlT3V0c2lkZXJzfGVufDB8fDE2NjgwNzEyMTE&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "19",
                "War and Peace",
                "Leo Tolstoy's historical epic that follows the lives of Russian aristocrats and soldiers during the Napoleonic Wars.",
                "Leo Tolstoy",
                "$22.00",
                "https://images.unsplash.com/photo-1579255106460-b3da9f74f459?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8MXx8V2FyJTIwYW5kJTIwUGVhY2V8fDB8fDE2NjgwNzE3MjA&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "20",
                "The Kite Runner",
                "Khaled Hosseini's novel that deals with the themes of redemption, guilt, and friendship against the backdrop of the political turmoil in Afghanistan.",
                "Khaled Hosseini",
                "$12.00",
                "https://images.unsplash.com/photo-1555625226-cfa97d58d512?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8Mnx8VGhlJTIwS2l0ZSUyMFJ1bm5lcnxlbnwwfDB8fDE2NjgwNzI4ODg&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "21",
                "The Girl on the Train",
                "Paula Hawkins' psychological thriller that revolves around the intertwining lives of three women and a mysterious disappearance.",
                "Paula Hawkins",
                "$14.99",
                "https://images.unsplash.com/photo-1519672560112-3d5bb72c51ca?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8Mnx8VGhlJTIwR2lybCUyMG9uJTIwdGhlJTIwVHJhaW58ZW58MHx8fDE2NjgwNzMzNjg&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "22",
                "The Silent Patient",
                "Alex Michaelides' psychological thriller that follows a woman who shoots her husband and then becomes mute, leaving everyone questioning her motives.",
                "Alex Michaelides",
                "$13.50",
                "https://images.unsplash.com/photo-1560192788-45ad71c3be8b?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8M3x8VGhlJTIwU2lsZW50JTIwUGF0aWVudHxlbnwwfDB8fDE2NjgwNzUzNjQ&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "23",
                "The Martian",
                "Andy Weir's gripping science fiction novel about an astronaut stranded on Mars and his efforts to survive against all odds.",
                "Andy Weir",
                "$16.00",
                "https://images.unsplash.com/photo-1592282326280-2d6d8e97bc6a?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8MXx8VGhlJTIwTWFydGlhbnxlbnwwfDB8fDE2NjgwNzYwMjc&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "24",
                "The Shining",
                "Stephen King's horror novel about a family isolated in a haunted hotel during the winter, unraveling a terrifying chain of events.",
                "Stephen King",
                "$19.99",
                "https://images.unsplash.com/photo-1572279452469-bf24b50e4dfd?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8M3x8VGhlJTIwU2hpbmluZyxlbnwwfDB8fDE2NjgwNzYyOTk&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "25",
                "Shogun",
                "James Clavell's historical novel set in 17th-century Japan, following the journey of an English navigator and his immersion into Japanese culture.",
                "James Clavell",
                "$20.00",
                "https://images.unsplash.com/photo-1590191475860-4f3fa6601d27?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8Mnx8U2hvZ3VufGVufDB8fDE2NjgwNzgyNzA&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "26",
                "The Godfather",
                "Mario Puzo's classic crime novel that tells the story of the powerful Corleone mafia family and their dealings in the world of organized crime.",
                "Mario Puzo",
                "$14.50",
                "https://images.unsplash.com/photo-1573664675323-4309ac3a55c2?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8MXx8VGhlJTIwR29kZmF0aGVyfGVufDB8fDE2NjgwNzkwNzQ&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "27",
                "The Picture of Dorian Gray",
                "Oscar Wilde's novel about a young man whose portrait ages while he remains youthful, exploring themes of beauty, morality, and hedonism.",
                "Oscar Wilde",
                "$11.99",
                "https://images.unsplash.com/photo-1531279200070-960d9eec58b1?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8MXx8VGhlJTIwUGljdHVyZSUyMG9mJTIwRG9yaWFuJTIwR3JheXxlbnwwfDB8fDE2NjgwNzkwNjI&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "28",
                "The Alchemist",
                "Paulo Coelho's novel about a young shepherd's journey to discover his personal legend and follow his dreams.",
                "Paulo Coelho",
                "$10.00",
                "https://images.unsplash.com/photo-1533032827113-4444b2f48ef7?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8Mnx8VGhlJTIwQWxjaGVtaXN0fGVufDB8fDE2NjgwNzkwNjA&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "29",
                "1984",
                "George Orwell's dystopian novel that explores themes of totalitarianism, surveillance, and mind control in a society ruled by a tyrannical government.",
                "George Orwell",
                "$9.99",
                "https://images.unsplash.com/photo-1600631282526-f7de2a0047be?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8Mnx8MTk4NHxlbnwwfDB8fDE2NjgwNzkwNTg&ixlib=rb-1.2.1&q=80&w=600"
            ),
            Book(
                "30",
                "Moby Dick",
                "Herman Melville's epic tale of the obsessive quest of Captain Ahab to seek revenge on the great white whale, Moby Dick.",
                "Herman Melville",
                "$18.99",
                "https://images.unsplash.com/photo-1560760966-16d9fc8e23b9?crop=entropy&cs=tinysrgb&fit=max&ixid=MnwzNjUyOXwwfDF8c2VhY2h8Mnx8TW9ieS1EaWNrfGVufDB8fDE2NjgwNzYzNzA&ixlib=rb-1.2.1&q=80&w=600"
            )
        )

        // Add each book to Firestore
        // Iterate over each book and add it to Firestore
        for (book in books) {
            booksCollection.document(book.id)
                .set(book)
                .addOnSuccessListener {
                    Log.d("Firestore", "Book added successfully: ${book.name}")
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error adding book: ${exception.message}")
                }
        }
    }
}