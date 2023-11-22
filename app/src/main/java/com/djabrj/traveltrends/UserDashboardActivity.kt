package com.djabrj.traveltrends

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity

class UserDashboardActivity : AppCompatActivity() {

    private lateinit var buttonBookHotel: Button
    private lateinit var buttonBookPackage: Button
    private lateinit var buttonLogout: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()

        // Get UI elements
        buttonBookHotel = findViewById(R.id.buttonBookHotel)
        buttonBookPackage = findViewById(R.id.buttonBookPackage)
        buttonLogout = findViewById(R.id.buttonLogout)

        // Set up button click listeners
        buttonBookHotel.setOnClickListener {

        }

        buttonBookPackage.setOnClickListener {
            startActivity(Intent(this, BookPackageActivity::class.java))
        }

        buttonLogout.setOnClickListener {
            logoutUser()
        }
    }


    private fun logoutUser() {
        auth.signOut()

        // After signing out, navigate to the login activity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
