package com.djabrj.traveltrends

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class BookedBy(
    val id: String,
    val name: String,
    val contact: String,

)

class BookedPackageAdapter(private var cardList: List<BookedBy>) : RecyclerView.Adapter<BookedPackageAdapter.PackageViewHolder>() {

    class PackageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val userName: TextView = itemView.findViewById(R.id.userName)
        val contact: TextView = itemView.findViewById(R.id.contact)
        val button: Button = itemView.findViewById(R.id.button)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.booked_by_card, parent, false)
        return PackageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        val travelPackage = cardList[position]
        holder.userName.text = travelPackage.name
        holder.contact.text = travelPackage.contact
        holder.button.visibility = View.VISIBLE
        holder.button.text = "Call"
        holder.button.setOnClickListener{
            call(it.context, travelPackage.contact)
        }
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    fun setPackages(packages: List<BookedBy>) {
        this.cardList = packages
        notifyDataSetChanged()
    }

    private fun call(context: Context, phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        context.startActivity(intent)
    }

}


class ShowPackageBookins : AppCompatActivity() {

    private lateinit var recyclerViewPackages: RecyclerView
    private lateinit var packageAdapter: BookedPackageAdapter
    val dataList = mutableListOf<BookedBy>()

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_package_bookins)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        // Get UI elements
        recyclerViewPackages = findViewById(R.id.recyclerViewPackages)


        // Set up RecyclerView
        packageAdapter = BookedPackageAdapter(dataList)
        recyclerViewPackages.layoutManager = LinearLayoutManager(this)
        recyclerViewPackages.adapter = packageAdapter

        // Get package details from Intent extras
        val packageId = intent.getStringExtra("packageId").toString()
        val packageName = intent.getStringExtra("packageName")

        // GEt fire base collection and Bookings
        // Get the reference to the document
        val documentReference = db.collection("travel_packages").document(packageId)

        // Retrieve the document
        documentReference.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Document exists, you can access its data
                    val data = documentSnapshot.data?.toMap()?.get("bookings_by") as List<*>
                    // Handle the document data as needed
                    for (item in data) {
                        getUserDetails(item.toString())
                    }
//                    println("Document data: $data")
                } else {
                    // Document does not exist
                    println("Document does not exist")
                }
            }
            .addOnFailureListener { e ->
                // Handle any errors that occurred during the document retrieval
                println("Error getting document: ${e.message}")
            }


    }

    fun getUserDetails(id : String){
        println(id)
        // Get the reference to the document
        val documentReference = db.collection("users").document(id)

        // Retrieve the document
        documentReference.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Document exists, you can access its data
                    val data = documentSnapshot.data
                    if (data != null) {
                        val travelPackage = BookedBy(
                            documentSnapshot.id,
                            data["name"] as String,
                            data["phone_number"] as String
                        )
                        dataList.add(travelPackage)
                        packageAdapter.notifyItemInserted(dataList.size - 1)
                        println("Document data: $travelPackage")


                    }


                    // Handle the document data as needed


                } else {
                    // Document does not exist
                    println("Document does not exist")
                }
            }
            .addOnFailureListener { e ->
                // Handle any errors that occurred during the document retrieval
                println("Error getting document: ${e.message}")
            }
    }



    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}