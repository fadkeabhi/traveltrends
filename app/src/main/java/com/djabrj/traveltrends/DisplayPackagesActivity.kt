package com.djabrj.traveltrends

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

data class TravelPackage(
    val id: String,
    val name: String,
    val description: String,
    val duration: Int,
    val price: Double,
    val availableSeats: Int
)



class PackageAdapter(private var cardList: List<TravelPackage>) : RecyclerView.Adapter<PackageAdapter.PackageViewHolder>() {

    class PackageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val packageNameTextView: TextView = itemView.findViewById(R.id.packageNameTextView)
        val packageDescriptionTextView: TextView = itemView.findViewById(R.id.packageDescriptionTextView)
        val packageDurationTextView: TextView = itemView.findViewById(R.id.packageDurationTextView)
        val packagePriceTextView: TextView = itemView.findViewById(R.id.packagePriceTextView)
        val availableSeatsTextView: TextView = itemView.findViewById(R.id.availableSeatsTextView)
        val button: Button = itemView.findViewById(R.id.button)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_package, parent, false)
        return PackageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        val travelPackage = cardList[position]
        holder.packageNameTextView.text = travelPackage.name
        holder.packageDescriptionTextView.text = travelPackage.description
        holder.packageDurationTextView.text = "Duration: ${travelPackage.duration} days"
        holder.packagePriceTextView.text = "Price: ${travelPackage.price} USD"
        holder.availableSeatsTextView.text = "Available Seats: ${travelPackage.availableSeats}"
        holder.button.visibility = View.VISIBLE
        holder.button.text = "Show Bookings"
        holder.button.setOnClickListener{
            val intent = Intent(it.context, ShowPackageBookins::class.java)
            intent.putExtra("packageId", travelPackage.id)
            intent.putExtra("packageName", travelPackage.name)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    fun setPackages(packages: List<TravelPackage>) {
        this.cardList = packages
        notifyDataSetChanged()
    }


}



class DisplayPackagesActivity : AppCompatActivity() {

    private lateinit var recyclerViewPackages: RecyclerView
    private lateinit var packageAdapter: PackageAdapter

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_packages)

        // Initialize Firebase
        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        // Get UI elements
        recyclerViewPackages = findViewById(R.id.recyclerViewPackages)

        val dataList = mutableListOf<TravelPackage>()
        // Set up RecyclerView
        packageAdapter = PackageAdapter(dataList)
        recyclerViewPackages.layoutManager = LinearLayoutManager(this)
        recyclerViewPackages.adapter = packageAdapter

        // Load and display packages
        loadPackages()
    }

    private fun loadPackages() {
        val currentUser = auth.currentUser

        // Check if the user is logged in
        if (currentUser != null) {
            // Get packages created by the current user (travel agency)
            db.collection("travel_packages")
                .whereEqualTo("created_by", currentUser.uid)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    displayPackages(querySnapshot)
                }
                .addOnFailureListener { e ->
                    showToast("Error getting packages: ${e.message}")
                }
        } else {
            // User is not logged in, handle accordingly (e.g., redirect to login screen)
            showToast("User not logged in")
        }
    }

    private fun displayPackages(querySnapshot: QuerySnapshot) {
        val packages = mutableListOf<TravelPackage>()

        for (document in querySnapshot.documents) {
            val packageData = document.data
            if (packageData != null) {
                val travelPackage = TravelPackage(
                    document.id,
                    packageData["name"] as String,
                    packageData["description"] as String,
                    (packageData["duration"] as Long).toInt(),
                    (packageData["price"] as Double),
                    (packageData["available_seats"] as Long).toInt()
                )
                packages.add(travelPackage)
            }
        }

        // Update the RecyclerView with the list of packages
        packageAdapter.setPackages(packages)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
