
package com.example.communitymedicineinventory

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MedicineDetailsActivity : AppCompatActivity() {

    private lateinit var detailId: TextView
    private lateinit var detailName: TextView
    private lateinit var detailCategory: TextView
    private lateinit var detailQuantity: TextView
    private lateinit var detailMinimum: TextView
    private lateinit var detailExpiration: TextView
    private lateinit var detailStatus: TextView

    private lateinit var editMedicineButton: Button
    private lateinit var backToInventoryButton: Button

    private var medicineId = ""
    private var userRole = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        medicineId = intent.getStringExtra("MEDICINE_ID") ?: ""
        userRole = intent.getStringExtra("USER_ROLE") ?: ""

        // Allow Admin, Inventory Custodian, and Health Worker.
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

        setContentView(R.layout.activity_medicine_details)

        detailId = findViewById(R.id.detailId)
        detailName = findViewById(R.id.detailName)
        detailCategory = findViewById(R.id.detailCategory)
        detailQuantity = findViewById(R.id.detailQuantity)
        detailMinimum = findViewById(R.id.detailMinimum)
        detailExpiration = findViewById(R.id.detailExpiration)
        detailStatus = findViewById(R.id.detailStatus)

        editMedicineButton = findViewById(R.id.editMedicineButton)
        backToInventoryButton = findViewById(R.id.backToInventoryButton)

        // Health Workers can only view medicine details.
        editMedicineButton.visibility =
            if (userRole == "health_worker") {
                View.GONE
            } else {
                View.VISIBLE
            }

        editMedicineButton.setOnClickListener {

            if (
                userRole != "admin" &&
                userRole != "inventory_custodian"
            ) {
                Toast.makeText(
                    this,
                    "You do not have permission to edit medicines.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val intent = Intent(
                this,
                AddEditMedicineActivity::class.java
            )

            intent.putExtra("MEDICINE_ID", medicineId)
            intent.putExtra("USER_ROLE", userRole)

            startActivity(intent)
        }

        backToInventoryButton.setOnClickListener {
            finish()
        }

        loadMedicine()
    }

    override fun onResume() {
        super.onResume()

        if (medicineId.isNotBlank()) {
            loadMedicine()
        }
    }

    private fun loadMedicine() {

        if (medicineId.isBlank()) {
            Toast.makeText(
                this,
                "Invalid medicine ID.",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        MedicineRepository.getById(
            medicineId,

            onResult = { medicine ->

                if (medicine == null) {
                    Toast.makeText(
                        this,
                        "Medicine not found.",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                    return@getById
                }

                detailId.text = "Medicine ID: ${medicine.medicineId}"
                detailName.text = medicine.name
                detailCategory.text = medicine.category

                detailQuantity.text =
                    "${medicine.quantity} ${medicine.unit}"

                detailMinimum.text =
                    medicine.minimumStock.toString()

                detailExpiration.text = medicine.expirationDate

                detailStatus.text =
                    if (medicine.isLowStock()) {
                        "Low Stock"
                    } else {
                        "Available"
                    }
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
}
