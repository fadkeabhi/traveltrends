package com.djabrj.traveltrends

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PackageDetailsActivity : AppCompatActivity() {

    private lateinit var textViewPackageName: TextView
    private lateinit var textViewPackageDescription: TextView
    private lateinit var textViewPackageDuration: TextView
    private lateinit var textViewPackagePrice: TextView
    private lateinit var buttonBookNow: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_package_details)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        // Get UI elements
        textViewPackageName = findViewById(R.id.textViewPackageName)
        textViewPackageDescription = findViewById(R.id.textViewPackageDescription)
        textViewPackageDuration = findViewById(R.id.textViewPackageDuration)
        textViewPackagePrice = findViewById(R.id.textViewPackagePrice)
        buttonBookNow = findViewById(R.id.buttonBookNow)

        // Get package details from Intent extras
        val packageId = intent.getStringExtra("packageId")
        val packageName = intent.getStringExtra("packageName")
        val packageDescription = intent.getStringExtra("packageDescription")
        val packageDuration = intent.getIntExtra("packageDuration", 0)
        val packagePrice = intent.getDoubleExtra("packagePrice", 0.0)

        // Set package details to UI elements
        textViewPackageName.text = packageName
        textViewPackageDescription.text = packageDescription
        textViewPackageDuration.text = "Duration: $packageDuration days"
        textViewPackagePrice.text = "Price: $packagePrice USD"

        // Set up button click listener for booking
        buttonBookNow.setOnClickListener {
            // Check if the user is logged in
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // Implement the booking logic here
                // For example, you can add a booking record to Firestore
                // and update the available seats for the travel package.
                bookPackage(currentUser.uid, packageId.toString())
            } else {
                // User is not logged in, handle accordingly (e.g., redirect to login screen)
                showToast("User not logged in")
            }
        }
    }

    private fun bookPackage(userId: String, packageId: String) {
        // Get a reference to the travel package document
        val packageRef = db.collection("travel_packages").document(packageId)

        // Update the travel package document to include the booking information
        packageRef
            .update(
                "bookings_by", FieldValue.arrayUnion(userId)
                // Add more fields as needed
            )
            .addOnSuccessListener {
                showToast("Booking successful!")

            }
            .addOnFailureListener { e ->
                showToast("Error booking package: ${e.message}")
            }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
