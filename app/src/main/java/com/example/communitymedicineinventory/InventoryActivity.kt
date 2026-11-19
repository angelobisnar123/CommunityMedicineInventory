
package com.example.communitymedicineinventory

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ValueEventListener

class InventoryActivity : AppCompatActivity() {

    private lateinit var searchInput: EditText
    private lateinit var addMedicineButton: Button
    private lateinit var medicineRecyclerView: RecyclerView
    private lateinit var backButton: Button

    private lateinit var adapter: MedicineAdapter

    private var allMedicines = listOf<Medicine>()
    private var databaseListener: ValueEventListener? = null

    private var userRole: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userRole = intent.getStringExtra("USER_ROLE") ?: ""

        // Allow only recognized roles.
        if (
            userRole != "admin" &&
            userRole != "inventory_custodian" &&
            userRole != "health_worker"
        ) {
            Toast.makeText(
                this,
                "Unauthorized access.",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        setContentView(R.layout.activity_inventory)

        searchInput = findViewById(R.id.searchInput)
        addMedicineButton = findViewById(R.id.addMedicineButton)
        medicineRecyclerView = findViewById(R.id.medicineRecyclerView)
        backButton = findViewById(R.id.backButton)

        // Only Admin and Inventory Custodian can add medicines.
        addMedicineButton.visibility =
            if (userRole == "health_worker") {
                View.GONE
            } else {
                View.VISIBLE
            }

        adapter = MedicineAdapter(emptyList()) { medicine ->

            val intent = Intent(
                this,
                MedicineDetailsActivity::class.java
            )

            intent.putExtra("MEDICINE_ID", medicine.medicineId)
            intent.putExtra("USER_ROLE", userRole)

            startActivity(intent)
        }

        medicineRecyclerView.layoutManager =
            LinearLayoutManager(this)

        medicineRecyclerView.adapter = adapter

        // Add medicine button.
        addMedicineButton.setOnClickListener {

            if (
                userRole != "admin" &&
                userRole != "inventory_custodian"
            ) {
                Toast.makeText(
                    this,
                    "You do not have permission to add medicine.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val intent = Intent(
                this,
                AddEditMedicineActivity::class.java
            )

            intent.putExtra("USER_ROLE", userRole)

            startActivity(intent)
        }

        // Back button.
        backButton.setOnClickListener {
            finish()
        }

        // Search medicine.
        searchInput.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                filterMedicines(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onStart() {
        super.onStart()

        if (
            userRole != "admin" &&
            userRole != "inventory_custodian" &&
            userRole != "health_worker"
        ) {
            return
        }

        databaseListener = MedicineRepository.observeAll(
            onDataChanged = { medicines ->

                allMedicines = medicines

                filterMedicines(
                    searchInput.text.toString()
                )
            },

            onError = { error ->
                Toast.makeText(
                    this,
                    error,
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    override fun onStop() {
        super.onStop()

        databaseListener?.let {
            MedicineRepository.removeListener(it)
        }

        databaseListener = null
    }

    private fun filterMedicines(query: String) {

        val filtered = allMedicines.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true)
        }

        adapter.updateList(filtered)
    }
}
