
package com.example.communitymedicineinventory

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StaffListActivity : AppCompatActivity() {

    private lateinit var staffRecyclerView: RecyclerView
    private lateinit var backToAdminButton: Button
    private lateinit var staffAdapter: StaffAdapter

    private val staffList = mutableListOf<Staff>()
    private var staffListener: ValueEventListener? = null

    private val usersRef = FirebaseDatabase.getInstance(
        "https://communitymedicineinventory-default-rtdb.asia-southeast1.firebasedatabase.app"
    ).getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_list)

        staffRecyclerView = findViewById(R.id.staffRecyclerView)
        backToAdminButton = findViewById(R.id.backToAdminButton)

        staffAdapter = StaffAdapter(staffList)

        staffRecyclerView.layoutManager = LinearLayoutManager(this)
        staffRecyclerView.adapter = staffAdapter

        backToAdminButton.setOnClickListener {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()

        staffListener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                staffList.clear()

                for (child in snapshot.children) {

                    val role = child.child("role")
                        .getValue(String::class.java) ?: ""

                    if (role == "inventory_custodian" ||
                        role == "health_worker") {

                        val staff = Staff(
                            uid = child.key ?: "",
                            name = child.child("name")
                                .getValue(String::class.java) ?: "",
                            email = child.child("email")
                                .getValue(String::class.java) ?: "",
                            role = role
                        )

                        staffList.add(staff)
                    }
                }

                staffAdapter.updateList(staffList)

                if (staffList.isEmpty()) {
                    Toast.makeText(
                        this@StaffListActivity,
                        "No staff accounts found.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(
                    this@StaffListActivity,
                    "Failed to load staff: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        usersRef.addValueEventListener(staffListener!!)
    }

    override fun onStop() {
        super.onStop()

        staffListener?.let {
            usersRef.removeEventListener(it)
        }

        staffListener = null
    }
}
