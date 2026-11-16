package com.example.communitymedicineinventory

import android.content.Intent
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        medicineId = intent.getStringExtra("MEDICINE_ID") ?: ""

        editMedicineButton.setOnClickListener {
            val intent = Intent(this, AddEditMedicineActivity::class.java)
            intent.putExtra("MEDICINE_ID", medicineId)
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

                detailStatus.text = if (medicine.isLowStock()) {
                    "Low Stock"
                } else {
                    "Available"
                }
            },
            onError = { error ->
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
        )
    }
}