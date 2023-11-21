package com.djabrj.traveltrends
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AddPackageActivity : AppCompatActivity() {

    private lateinit var editTextPackageName: EditText
    private lateinit var editTextPackageDescription: EditText
    private lateinit var editTextPackageDuration: EditText
    private lateinit var editTextPackagePrice: EditText
    private lateinit var editTextAvailableSeats: EditText
    private lateinit var buttonAddPackage: Button

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_package)

        // Initialize Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Get UI elements
        editTextPackageName = findViewById(R.id.editTextPackageName)
        editTextPackageDescription = findViewById(R.id.editTextPackageDescription)
        editTextPackageDuration = findViewById(R.id.editTextPackageDuration)
        editTextPackagePrice = findViewById(R.id.editTextPackagePrice)
        editTextAvailableSeats = findViewById(R.id.editTextAvailableSeats)
        buttonAddPackage = findViewById(R.id.buttonAddPackage)

        // Set up button click listener
        buttonAddPackage.setOnClickListener {
            addNewPackage()
        }
    }

    private fun addNewPackage() {
        val packageName = editTextPackageName.text.toString().trim()
        val packageDescription = editTextPackageDescription.text.toString().trim()
        val packageDuration = editTextPackageDuration.text.toString().toIntOrNull() ?: 0
        val packagePrice = editTextPackagePrice.text.toString().toDoubleOrNull() ?: 0.0
        val availableSeats = editTextAvailableSeats.text.toString().toIntOrNull() ?: 0

        if (packageName.isNotEmpty() && packageDescription.isNotEmpty() && packageDuration > 0 &&
            packagePrice > 0.0 && availableSeats > 0) {

            val packageId = UUID.randomUUID().toString()
            val createdBy = auth.currentUser?.uid

            val packageData = hashMapOf(
                "name" to packageName,
                "description" to packageDescription,
                "duration" to packageDuration,
                "price" to packagePrice,
                "available_seats" to availableSeats,
                "created_by" to createdBy,
                "bookings_by" to emptyList<String>(), // Initially, there are no bookings
                "created_at" to FieldValue.serverTimestamp(),
                "updated_at" to FieldValue.serverTimestamp()
            )

            db.collection("travel_packages")
                .document(packageId)
                .set(packageData)
                .addOnSuccessListener {
                    showToast("Package added successfully")
                    finish()
                }
                .addOnFailureListener { e ->
                    showToast("Error adding package: ${e.message}")
                }

        } else {
            showToast("Please fill in all fields with valid values")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
