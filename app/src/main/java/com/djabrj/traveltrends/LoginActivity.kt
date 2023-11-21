package com.djabrj.traveltrends

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Check if user already logged in
        if (auth.currentUser != null) {
            checkUserRole(auth.currentUser?.uid ?: "")
        }

        // Get UI elements
        val editTextEmail: EditText = findViewById(R.id.editTextEmail)
        val editTextPassword: EditText = findViewById(R.id.editTextPassword)
        val buttonLogin: Button = findViewById(R.id.buttonLogin)
        val buttonSignup: Button = findViewById(R.id.buttonSignup)

        // Set up login button click listener
        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up signup button click listener
        buttonSignup.setOnClickListener {

            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // After successful login, check user role
                    checkUserRole(auth.currentUser?.uid ?: "")
                } else {
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

     private fun checkUserRole(userId: String) {
        val currentUserRef = db.collection("users").document(userId)

        currentUserRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val roles = documentSnapshot["roles"] as? List<String>

                    if (roles?.contains("user") == true) {
                        // User role
                        Toast.makeText(this, "Logged in as a regular user", Toast.LENGTH_SHORT).show()
                    }
                    if (roles?.contains("hotel_owner") == true) {
                        // Hotel owner role
                        Toast.makeText(this, "Logged in as a hotel owner", Toast.LENGTH_SHORT).show()
                        // Redirect to hotel owner's activity or perform other actions
                    }
                    if (roles?.contains("travel_agency") == true) {
                        // Travel agency role
                        Toast.makeText(this, "Logged in as a travel agency", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, TravelAgencyDashboardActivity::class.java)
                        finish()
                        startActivity(intent)
                        // Redirect to travel agency's activity or perform other actions
                    }
                } else {
                    // Handle the case where the user document doesn't exist
                    Toast.makeText(this, "User document not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                // Handle exceptions
                Toast.makeText(this, "Error getting user role", Toast.LENGTH_SHORT).show()
            }
    }
}
