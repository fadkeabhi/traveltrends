package com.djabrj.traveltrends

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Get UI elements
        val editTextName: EditText = findViewById(R.id.editTextName)
        val editTextPhoneNumber: EditText = findViewById(R.id.editTextPhoneNumber)
        val editTextEmail: EditText = findViewById(R.id.editTextEmail)
        val editTextPassword: EditText = findViewById(R.id.editTextPassword)
        val buttonSignup: Button = findViewById(R.id.buttonSignup)

        // Set up signup button click listener
        buttonSignup.setOnClickListener {
            val name = editTextName.text.toString().trim()
            val phoneNumber = editTextPhoneNumber.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (name.isNotEmpty() && phoneNumber.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                signupUser(name, phoneNumber, email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signupUser(name: String, phoneNumber: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // After successful signup, set default user role to "user" and store additional details
                    val userId = auth.currentUser?.uid ?: ""
                    setDefaultUserRoleAndDetails(userId, name, phoneNumber, email)

                    Toast.makeText(baseContext, "Signup successful.", Toast.LENGTH_SHORT).show()
                    finish() // You might want to navigate to the login screen or another activity
                } else {
                    Toast.makeText(baseContext, "Signup failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setDefaultUserRoleAndDetails(userId: String, name: String, phoneNumber: String, email : String) {
        val userRef = db.collection("users").document(userId)

        // Set default user role to "user" and store additional details
        val userData = hashMapOf(
            "name" to name,
            "phone_number" to phoneNumber,
            "email" to email,
            "roles" to listOf("user")
        )

        userRef.set(userData)
            .addOnSuccessListener {
                // Successfully set user role and stored additional details
            }
            .addOnFailureListener { e ->
                Toast.makeText(baseContext, "Error setting user role and storing details", Toast.LENGTH_SHORT).show()
            }
    }
}
