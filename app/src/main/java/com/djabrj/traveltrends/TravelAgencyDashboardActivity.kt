package com.djabrj.traveltrends

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class TravelAgencyDashboardActivity : AppCompatActivity() {

    private lateinit var textViewWelcome: TextView
    private lateinit var buttonAddPackage: Button
    private lateinit var buttonDisplayPackages: Button
    private lateinit var buttonConfirmUserPurchase: Button
    private lateinit var buttonLogout: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel_agency_dashboard)

        auth = FirebaseAuth.getInstance()

        // Get UI elements
        textViewWelcome = findViewById(R.id.textViewWelcome)
        buttonAddPackage = findViewById(R.id.buttonAddPackage)
        buttonDisplayPackages = findViewById(R.id.buttonDisplayPackages)
        buttonConfirmUserPurchase = findViewById(R.id.buttonConfirmUserPurchase)
        buttonLogout = findViewById(R.id.buttonLogout)

        // Set welcome message
        val travelAgencyName = "Your Travel Agency" // Replace with actual agency name
        textViewWelcome.text = "Welcome, $travelAgencyName!"

        // Set up button click listeners
        buttonAddPackage.setOnClickListener {
            // Handle add package action
            // For simplicity, this example just displays a toast message
            startActivity(Intent(this, AddPackageActivity::class.java))
//            showToast("Add New Package clicked")
        }

        buttonDisplayPackages.setOnClickListener {
            // Handle display packages action
            // For simplicity, this example just displays a toast message
            startActivity(Intent(this, DisplayPackagesActivity::class.java))
//            showToast("Display Current Packages clicked")
        }

        buttonConfirmUserPurchase.setOnClickListener {
            // Handle confirm user purchases action
            // For simplicity, this example just displays a toast message
            showToast("Confirm User Purchases clicked")
        }

        buttonLogout.setOnClickListener {

            auth.signOut()

            // After signing out, navigate to the login activity
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun showToast(message: String) {
        // Helper method to display toast messages
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
