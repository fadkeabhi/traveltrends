package com.djabrj.traveltrends

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase



class BookPackageAdapter(private var cardList: List<TravelPackage>) : RecyclerView.Adapter<BookPackageAdapter.BookPackageViewHolder>() {

    class BookPackageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val packageNameTextView: TextView = itemView.findViewById(R.id.packageNameTextView)
        val packageDescriptionTextView: TextView = itemView.findViewById(R.id.packageDescriptionTextView)
        val packageDurationTextView: TextView = itemView.findViewById(R.id.packageDurationTextView)
        val packagePriceTextView: TextView = itemView.findViewById(R.id.packagePriceTextView)
        val availableSeatsTextView: TextView = itemView.findViewById(R.id.availableSeatsTextView)
        val button: Button = itemView.findViewById(R.id.button)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookPackageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_package, parent, false)
        return BookPackageViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookPackageViewHolder, position: Int) {
        val travelPackage = cardList[position]
        holder.packageNameTextView.text = travelPackage.name
        holder.packageDescriptionTextView.text = travelPackage.description
        holder.packageDurationTextView.text = "Duration: ${travelPackage.duration} days"
        holder.packagePriceTextView.text = "Price: ${travelPackage.price} USD"
        holder.availableSeatsTextView.text = "Available Seats: ${travelPackage.availableSeats}"
        holder.button.visibility = View.VISIBLE
        holder.button.text = "More Details"
        holder.button.setOnClickListener{
            navigateToPackageDetails(travelPackage, it.context)
        }
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    fun setPackages(packages: List<TravelPackage>) {
        this.cardList = packages
        notifyDataSetChanged()
    }


    private fun navigateToPackageDetails(selectedPackage: TravelPackage, context: Context) {
        // You can implement the logic to navigate to the package details activity
        // For example, you can create a new activity called PackageDetailsActivity
        // and pass the selected package details using Intent extras.
        val intent = Intent(context, PackageDetailsActivity::class.java)
        intent.putExtra("packageId", selectedPackage.id)
        intent.putExtra("packageName", selectedPackage.name)
        intent.putExtra("packageDescription", selectedPackage.description)
        intent.putExtra("packageDuration", selectedPackage.duration)
        intent.putExtra("packagePrice", selectedPackage.price)
        intent.putExtra("availableSeats", selectedPackage.availableSeats)
        context.startActivity(intent)
    }





}

class BookPackageActivity : AppCompatActivity() {

    private lateinit var recyclerViewAvailablePackages: RecyclerView
    private lateinit var bookPackageAdapter: BookPackageAdapter

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_package)

        // Initialize Firebase
        db = Firebase.firestore

        // Get UI elements
        recyclerViewAvailablePackages = findViewById(R.id.recyclerViewAvailablePackages)

        val dataList = mutableListOf<TravelPackage>()

        // Set up RecyclerView
        bookPackageAdapter = BookPackageAdapter(dataList)
        recyclerViewAvailablePackages.layoutManager = LinearLayoutManager(this)
        recyclerViewAvailablePackages.adapter = bookPackageAdapter

        // Load and display available packages
        loadAvailablePackages()
    }

    private fun loadAvailablePackages() {
        // Get all available travel packages
        db.collection("travel_packages")
            .get()
            .addOnSuccessListener { querySnapshot ->
                displayAvailablePackages(querySnapshot)
            }
            .addOnFailureListener { e ->
                showToast("Error getting available packages: ${e.message}")
            }
    }

    private fun displayAvailablePackages(querySnapshot: QuerySnapshot) {
        val availablePackages = mutableListOf<TravelPackage>()

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
                availablePackages.add(travelPackage)
            }
        }

        // Update the RecyclerView with the list of available packages
        bookPackageAdapter.setPackages(availablePackages)
    }



    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
